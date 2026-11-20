package hospital.service;

import hospital.model.Doctor;
import hospital.model.TimeSlot;

public class SlotService {
    private String timezone;
    private int standardSlotMinutes;
    private int defaultHoldMinutes;
    private TimeSlot[] slots;
    private Doctor[] doctors;

    public SlotService(TimeSlot[] slots, Doctor[] doctors) {
        this.timezone = "Asia/Karachi";
        this.standardSlotMinutes = 30;
        this.defaultHoldMinutes = 10;
        this.slots = slots;
        this.doctors = doctors;
    }

    public TimeSlot[] searchSlots(String specialty, String location, String date) {
        TimeSlot[] matches = new TimeSlot[slots.length];
        int count = 0;
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != null && slots[i].isAvailable()
                    && slots[i].getStartAt().startsWith(date)) {
                Doctor doctor = findDoctor(slots[i].getDoctorId());
                if (doctor != null && doctor.getSpecialty().toLowerCase().contains(specialty.toLowerCase())
                        && doctor.getClinicLocation().toLowerCase().contains(location.toLowerCase())) {
                    matches[count] = slots[i];
                    count++;
                }
            }
        }
        return matches;
    }

    public TimeSlot[] searchSlots(String specialty) {
        return searchSlots(specialty, "", "");
    }

    public boolean reserveSlot(String slotId, String patientId) {
        TimeSlot slot = findSlot(slotId);
        return slot != null && slot.placeHold(patientId, defaultHoldMinutes);
    }

    public void releaseSlot(String slotId) {
        TimeSlot slot = findSlot(slotId);
        if (slot != null) slot.releaseHold();
    }

    public boolean swapSlots(String oldSlotId, String newSlotId, String appointmentId) {
        TimeSlot oldSlot = findSlot(oldSlotId);
        TimeSlot newSlot = findSlot(newSlotId);
        return oldSlot != null && newSlot != null && oldSlot != newSlot
                && oldSlot.getDoctorId().equals(newSlot.getDoctorId())
                && oldSlot.swapWith(newSlot, appointmentId);
    }

    public boolean updateDoctorSchedule(String doctorId, String hours, String blackoutDate) {
        Doctor doctor = findDoctor(doctorId);
        if (doctor == null || !doctor.setWeeklyHours(hours)) return false;
        if (blackoutDate.length() > 0) doctor.addBlackout(blackoutDate, "Doctor request");
        return true;
    }

    public TimeSlot findSlot(String slotId) {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != null && slots[i].getSlotId().equalsIgnoreCase(slotId)) return slots[i];
        }
        return null;
    }

    public Doctor findDoctor(String doctorId) {
        for (int i = 0; i < doctors.length; i++) {
            if (doctors[i] != null && doctors[i].getUserId().equalsIgnoreCase(doctorId)) return doctors[i];
        }
        return null;
    }

    public String getTimezone() { return timezone; }
    public void setTimezone(String value) { timezone = value; }
    public int getStandardSlotMinutes() { return standardSlotMinutes; }
    public void setStandardSlotMinutes(int value) { standardSlotMinutes = value; }
    public int getDefaultHoldMinutes() { return defaultHoldMinutes; }
    public void setDefaultHoldMinutes(int value) { defaultHoldMinutes = value; }
    public TimeSlot[] getSlots() { return slots; }
    public void setSlots(TimeSlot[] value) { slots = value; }
    public Doctor[] getDoctors() { return doctors; }
    public void setDoctors(Doctor[] value) { doctors = value; }
}
