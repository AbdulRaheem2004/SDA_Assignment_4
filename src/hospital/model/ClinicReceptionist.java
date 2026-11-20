package hospital.model;

public class ClinicReceptionist extends User {
    private String staffId;
    private String clinicId;
    private String scannerId;
    private String shiftStart;

    public ClinicReceptionist(String id, String name, String staffId, String clinicId, String scannerId) {
        super(id, name, "reception@demo.local", "03000000000", "demo123");
        this.staffId = staffId;
        this.clinicId = clinicId;
        this.scannerId = scannerId;
        this.shiftStart = "09:00";
    }

    public boolean scanQrPass(String qrToken, Appointment[] appointments) {
        for (int i = 0; i < appointments.length; i++) {
            if (appointments[i] != null && appointments[i].getQrToken().equals(qrToken)) {
                return markCheckedIn(appointments[i]);
            }
        }
        return false;
    }

    public boolean markCheckedIn(Appointment appointment) {
        return appointment != null && appointment.markCheckedIn(scannerId);
    }

    public int viewWaitingQueue(Appointment[] appointments) {
        int count = 0;
        for (int i = 0; i < appointments.length; i++) {
            if (appointments[i] != null && appointments[i].getStatus().equals("CONFIRMED")) count++;
        }
        return count;
    }

    @Override
    public String getRolePermissions() { return "Scan QR passes and check in patients"; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String value) { staffId = value; }
    public String getClinicId() { return clinicId; }
    public void setClinicId(String value) { clinicId = value; }
    public String getScannerId() { return scannerId; }
    public void setScannerId(String value) { scannerId = value; }
    public String getShiftStart() { return shiftStart; }
    public void setShiftStart(String value) { shiftStart = value; }
}
