package hospital.model;

public class TimeSlot {
    private String slotId;
    private String doctorId;
    private String startAt;
    private String endAt;
    private String status;
    private String heldByPatientId;
    private String appointmentId;
    private int holdMinutesRemaining;

    public TimeSlot(String slotId, String doctorId, String startAt, String endAt) {
        this.slotId = slotId;
        this.doctorId = doctorId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = "OPEN";
        this.heldByPatientId = "";
        this.appointmentId = "";
        this.holdMinutesRemaining = 0;
    }

    public boolean isAvailable() { return status.equals("OPEN"); }

    public boolean placeHold(String patientId, int minutes) {
        if (!isAvailable() || patientId.length() == 0 || minutes <= 0) return false;
        status = "HELD";
        heldByPatientId = patientId;
        holdMinutesRemaining = minutes;
        return true;
    }

    public void releaseHold() {
        status = "OPEN";
        heldByPatientId = "";
        appointmentId = "";
        holdMinutesRemaining = 0;
    }

    public boolean markBooked(String appointmentId) {
        if (!status.equals("HELD")) return false;
        status = "BOOKED";
        this.appointmentId = appointmentId;
        return true;
    }

    public boolean swapWith(TimeSlot replacement, String newAppointmentId) {
        if (!status.equals("BOOKED") || !replacement.placeHold(heldByPatientId, 10)) return false;
        replacement.markBooked(newAppointmentId);
        releaseHold();
        return true;
    }

    public String getSlotId() { return slotId; }
    public void setSlotId(String value) { slotId = value; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String value) { doctorId = value; }
    public String getStartAt() { return startAt; }
    public void setStartAt(String value) { startAt = value; }
    public String getEndAt() { return endAt; }
    public void setEndAt(String value) { endAt = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
    public String getHeldByPatientId() { return heldByPatientId; }
    public void setHeldByPatientId(String value) { heldByPatientId = value; }
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String value) { appointmentId = value; }
    public int getHoldMinutesRemaining() { return holdMinutesRemaining; }
    public void setHoldMinutesRemaining(int value) { holdMinutesRemaining = value; }
}
