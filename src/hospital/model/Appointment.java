package hospital.model;

public class Appointment {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String slotId;
    private String scheduledAt;
    private String status;
    private String chiefComplaint;
    private String qrToken;
    private double depositAmount;
    private String paymentTransactionId;
    private int hoursFromBooking;
    private String cancellationReason;

    public Appointment(String id, String patientId, String doctorId, String slotId,
                       String scheduledAt, String complaint, double deposit, int hoursFromBooking) {
        this.appointmentId = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.slotId = slotId;
        this.scheduledAt = scheduledAt;
        this.chiefComplaint = complaint;
        this.depositAmount = deposit;
        this.hoursFromBooking = hoursFromBooking;
        this.status = "PENDING";
        this.qrToken = "QR-" + id;
        this.paymentTransactionId = "";
        this.cancellationReason = "";
    }

    public boolean confirm(String transactionId) {
        if (!status.equals("PENDING") || transactionId.length() == 0) return false;
        paymentTransactionId = transactionId;
        status = "CONFIRMED";
        return true;
    }

    public boolean cancel(String reason) {
        if (!status.equals("CONFIRMED")) return false;
        status = "CANCELLED";
        cancellationReason = reason;
        return true;
    }

    public boolean reschedule(String newSlotId, String newTime, int newHoursFromBooking) {
        if (!status.equals("CONFIRMED")) return false;
        slotId = newSlotId;
        scheduledAt = newTime;
        hoursFromBooking = newHoursFromBooking;
        return true;
    }

    public boolean markCheckedIn(String scannerId) {
        if (!status.equals("CONFIRMED") || scannerId.length() == 0) return false;
        status = "CHECKED_IN";
        return true;
    }

    public int hoursUntilStart(int elapsedHours) {
        return hoursFromBooking - elapsedHours;
    }

    public boolean complete(String notes) {
        if (!status.equals("CHECKED_IN")) return false;
        status = "COMPLETED";
        return true;
    }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String value) { appointmentId = value; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String value) { patientId = value; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String value) { doctorId = value; }
    public String getSlotId() { return slotId; }
    public void setSlotId(String value) { slotId = value; }
    public String getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(String value) { scheduledAt = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
    public String getChiefComplaint() { return chiefComplaint; }
    public void setChiefComplaint(String value) { chiefComplaint = value; }
    public String getQrToken() { return qrToken; }
    public void setQrToken(String value) { qrToken = value; }
    public double getDepositAmount() { return depositAmount; }
    public void setDepositAmount(double value) { depositAmount = value; }
    public String getPaymentTransactionId() { return paymentTransactionId; }
    public void setPaymentTransactionId(String value) { paymentTransactionId = value; }
    public int getHoursFromBooking() { return hoursFromBooking; }
    public void setHoursFromBooking(int value) { hoursFromBooking = value; }
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String value) { cancellationReason = value; }
}
