package hospital.service;

import hospital.model.Appointment;
import hospital.model.Doctor;
import hospital.model.Patient;
import hospital.model.PaymentTransaction;
import hospital.model.TimeSlot;

public class AppointmentMgr {
    private int holdDurationMinutes;
    private int cancellationNoticeHours;
    private double lateCancellationPercent;
    private String currentSessionId;
    private SlotService slotService;
    private PaymentService paymentService;
    private ApptDB database;
    private AuditLog auditLog;
    private NotificationChannel[] notificationChannels;
    private Patient patient;
    private String lastMessage;
    private double lastRefundAmount;

    public AppointmentMgr(SlotService slots, PaymentService payments, ApptDB database,
                          AuditLog audit, NotificationChannel[] channels, Patient patient) {
        this.holdDurationMinutes = 10;
        this.cancellationNoticeHours = 24;
        this.lateCancellationPercent = 50;
        this.currentSessionId = "SESSION-1";
        this.slotService = slots;
        this.paymentService = payments;
        this.database = database;
        this.auditLog = audit;
        this.notificationChannels = channels;
        this.patient = patient;
        this.lastMessage = "Ready";
        this.lastRefundAmount = 0;
    }

    public TimeSlot[] searchSlots(String specialty, String location, String date) {
        return slotService.searchSlots(specialty, location, date);
    }

    public boolean holdSlot(String patientId, String slotId) {
        TimeSlot slot = slotService.findSlot(slotId);
        if (slot == null || !slot.placeHold(patientId, holdDurationMinutes)) {
            lastMessage = "Slot not found or already reserved.";
            auditLog.record(patientId, "BOOKING_HOLD_FAILED", "TimeSlot", slotId, "DENIED");
            return false;
        }
        return true;
    }

    public Appointment bookAppointment(String patientId, String slotId, String complaint,
                                       String paymentDetails, int hoursUntilVisit) {
        TimeSlot slot = slotService.findSlot(slotId);
        Doctor doctor = slot == null ? null : slotService.findDoctor(slot.getDoctorId());
        if (!patient.getUserId().equals(patientId) || doctor == null || complaint.trim().length() == 0
                || hoursUntilVisit <= 0) {
            lastMessage = "Invalid patient, slot, complaint or hours until visit.";
            return null;
        }
        if (!holdSlot(patientId, slotId)) return null;

        PaymentTransaction payment = processPayment(patientId, doctor.getConsultationFee(), paymentDetails);
        if (payment == null || !payment.isSuccessful()) {
            slotService.releaseSlot(slotId);
            lastMessage = "Payment declined. Slot released; no appointment created.";
            auditLog.record(patientId, "PAYMENT_DECLINED", "TimeSlot", slotId, "DENIED");
            return null;
        }

        Appointment appointment = buildAppointment(patientId, slot, complaint,
                doctor.getConsultationFee(), hoursUntilVisit);
        if (!database.saveAppointment(appointment)) {
            paymentService.refund(payment.getTransactionId(), payment.getAmount());
            slotService.releaseSlot(slotId);
            lastMessage = "Appointment capacity reached; simulated payment refunded.";
            return null;
        }
        payment.setAppointmentId(appointment.getAppointmentId());
        appointment.confirm(payment.getTransactionId());
        slot.markBooked(appointment.getAppointmentId());
        auditLog.record(patientId, "BOOKING_CONFIRMED", "Appointment",
                appointment.getAppointmentId(), "SUCCESS");
        notifyConfirmation(appointment);
        lastMessage = paymentService.createReceipt(payment.getTransactionId());
        return appointment;
    }

    private Appointment buildAppointment(String patientId, TimeSlot slot, String complaint,
                                         double deposit, int hoursUntilVisit) {
        return new Appointment("A-" + (database.getAppointmentCount() + 1), patientId,
                slot.getDoctorId(), slot.getSlotId(), slot.getStartAt(), complaint, deposit, hoursUntilVisit);
    }

    public PaymentTransaction processPayment(String patientId, double amount, String details) {
        return paymentService.authorizeDeposit(patientId, amount, details);
    }

    public boolean cancelOrReschedule(String appointmentId, String action, String newSlotId,
                                      int elapsedHours, int newHoursUntilVisit) {
        Appointment appointment = database.findAppointment(appointmentId);
        if (appointment == null || !appointment.getStatus().equals("CONFIRMED")
                || !appointment.getPatientId().equals(patient.getUserId())) {
            lastMessage = "Only a confirmed appointment belonging to this patient can be changed.";
            return false;
        }

        if (action.equalsIgnoreCase("cancel")) {
            if (elapsedHours < 0 || elapsedHours > appointment.getHoursFromBooking()) {
                lastMessage = "Elapsed hours must be between 0 and the original hours until visit.";
                return false;
            }
            int hoursLeft = appointment.hoursUntilStart(elapsedHours);
            double refundPercent = hoursLeft >= cancellationNoticeHours
                    ? 100.0 : 100.0 - lateCancellationPercent;
            PaymentTransaction payment = paymentService.findTransaction(appointment.getPaymentTransactionId());
            if (payment == null) {
                lastMessage = "No matching payment transaction.";
                return false;
            }
            double refund = payment.calculateRefund(refundPercent);
            if (!paymentService.refund(payment.getTransactionId(), refund)) {
                lastMessage = "Refund simulation failed; booking unchanged.";
                return false;
            }
            appointment.cancel("Patient cancellation");
            slotService.releaseSlot(appointment.getSlotId());
            lastRefundAmount = refund;
            lastMessage = "Cancelled. " + refundPercent + "% simulated refund = PKR " + refund;
            auditLog.record(patient.getUserId(), "APPOINTMENT_CANCELLED", "Appointment",
                    appointmentId, "SUCCESS");
            notifyCancellation(appointment);
            return true;
        } else if (action.equalsIgnoreCase("reschedule")) {
            TimeSlot replacement = slotService.findSlot(newSlotId);
            if (replacement == null || newHoursUntilVisit <= 0
                    || !slotService.swapSlots(appointment.getSlotId(), newSlotId, appointmentId)) {
                lastMessage = "New slot unavailable or belongs to another doctor.";
                return false;
            }
            appointment.reschedule(newSlotId, replacement.getStartAt(), newHoursUntilVisit);
            lastMessage = "Rescheduled to " + replacement.getStartAt() + " (deposit transferred).";
            auditLog.record(patient.getUserId(), "APPOINTMENT_RESCHEDULED", "Appointment",
                    appointmentId, "SUCCESS");
            notifyConfirmation(appointment);
            return true;
        }
        lastMessage = "Unknown action.";
        return false;
    }

    public boolean checkIn(String qrToken, String receptionistId) {
        Appointment appointment = database.findByQr(qrToken);
        if (appointment == null || !appointment.markCheckedIn(receptionistId)) {
            lastMessage = "QR pass not found or appointment not eligible for check-in.";
            return false;
        }
        auditLog.record(receptionistId, "PATIENT_CHECKED_IN", "Appointment",
                appointment.getAppointmentId(), "SUCCESS");
        lastMessage = "Checked in appointment " + appointment.getAppointmentId();
        return true;
    }

    private void notifyConfirmation(Appointment appointment) {
        for (int i = 0; i < notificationChannels.length; i++) {
            NotificationChannel channel = notificationChannels[i];
            String destination = channel.getChannelName().equals("SMS") ? patient.getPhone() : patient.getEmail();
            channel.send(destination, "Appointment confirmed", channel.formatConfirmation(appointment));
        }
    }

    private void notifyCancellation(Appointment appointment) {
        for (int i = 0; i < notificationChannels.length; i++) {
            NotificationChannel channel = notificationChannels[i];
            String destination = channel.getChannelName().equals("SMS") ? patient.getPhone() : patient.getEmail();
            channel.send(destination, "Appointment cancelled", channel.formatCancellation(appointment));
        }
    }

    public int getHoldDurationMinutes() { return holdDurationMinutes; }
    public void setHoldDurationMinutes(int value) { holdDurationMinutes = value; }
    public int getCancellationNoticeHours() { return cancellationNoticeHours; }
    public void setCancellationNoticeHours(int value) { cancellationNoticeHours = value; }
    public double getLateCancellationPercent() { return lateCancellationPercent; }
    public void setLateCancellationPercent(double value) { lateCancellationPercent = value; }
    public String getCurrentSessionId() { return currentSessionId; }
    public void setCurrentSessionId(String value) { currentSessionId = value; }
    public String getLastMessage() { return lastMessage; }
    public double getLastRefundAmount() { return lastRefundAmount; }
}
