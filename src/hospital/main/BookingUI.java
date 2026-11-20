package hospital.main;

import hospital.model.Appointment;
import hospital.model.Doctor;
import hospital.model.TimeSlot;
import hospital.service.AppointmentMgr;
import hospital.service.SlotService;

public class BookingUI {
    private String sessionId;
    private String selectedSpecialty;
    private String selectedLocation;
    private String selectedSlotId;
    private AppointmentMgr manager;
    private SlotService slotService;

    public BookingUI(String sessionId, AppointmentMgr manager, SlotService slotService) {
        this.sessionId = sessionId;
        this.manager = manager;
        this.slotService = slotService;
        this.selectedSpecialty = "";
        this.selectedLocation = "";
        this.selectedSlotId = "";
    }

    public TimeSlot[] requestSlots(String specialty, String location, String date) {
        selectedSpecialty = specialty;
        selectedLocation = location;
        return manager.searchSlots(specialty, location, date);
    }

    public void displaySlots(TimeSlot[] slots) {
        int found = 0;
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != null) {
                Doctor doctor = slotService.findDoctor(slots[i].getDoctorId());
                System.out.println(slots[i].getSlotId() + " | " + slots[i].getStartAt()
                        + " | " + doctor.getFullName() + " | " + doctor.getSpecialty()
                        + " | " + doctor.getClinicLocation() + " | PKR " + doctor.getConsultationFee());
                found++;
            }
        }
        if (found == 0) showError("No open appointments match the search.");
    }

    public void selectSlot(String slotId) { selectedSlotId = slotId; }

    public Appointment submitBooking(String patientId, String complaint, String paymentDetails,
                                     int hoursUntilVisit) {
        return manager.bookAppointment(patientId, selectedSlotId, complaint, paymentDetails, hoursUntilVisit);
    }

    public void showBookingConfirmation(String appointmentId, String qrToken) {
        System.out.println("BOOKING CONFIRMED | Appointment: " + appointmentId + " | QR: " + qrToken);
        System.out.println(manager.getLastMessage());
    }

    public void showError(String message) { System.out.println("ERROR: " + message); }

    public boolean requestCancelOrReschedule(String appointmentId, String action,
                                              String newSlotId, int elapsedHours, int newHours) {
        return manager.cancelOrReschedule(appointmentId, action, newSlotId, elapsedHours, newHours);
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String value) { sessionId = value; }
    public String getSelectedSpecialty() { return selectedSpecialty; }
    public void setSelectedSpecialty(String value) { selectedSpecialty = value; }
    public String getSelectedLocation() { return selectedLocation; }
    public void setSelectedLocation(String value) { selectedLocation = value; }
    public String getSelectedSlotId() { return selectedSlotId; }
    public void setSelectedSlotId(String value) { selectedSlotId = value; }
}
