package hospital.model;

public class Doctor extends User {
    private String providerNumber;
    private String specialty;
    private String clinicLocation;
    private double consultationFee;
    private String weeklyHours;
    private String blackoutDate;

    public Doctor(String id, String name, String email, String phone,
                  String providerNumber, String specialty, String location, double fee) {
        super(id, name, email, phone, "demo123");
        this.providerNumber = providerNumber;
        this.specialty = specialty;
        this.clinicLocation = location;
        this.consultationFee = fee;
        this.weeklyHours = "Mon-Fri 09:00-17:00";
        this.blackoutDate = "None";
    }

    public Doctor(String id, String name, String specialty, String location, double fee) {
        this(id, name, "doctor@demo.local", "", "PR-" + id, specialty, location, fee);
    }

    public boolean setWeeklyHours(String hours) {
        if (hours == null || hours.trim().length() == 0) return false;
        weeklyHours = hours;
        return true;
    }

    public void addBlackout(String date, String reason) {
        blackoutDate = date + " (" + reason + ")";
    }

    public int viewDailyRoster(Appointment[] appointments, String date) {
        int count = 0;
        for (int i = 0; i < appointments.length; i++) {
            if (appointments[i] != null && appointments[i].getDoctorId().equals(getUserId())
                    && appointments[i].getScheduledAt().startsWith(date)
                    && !appointments[i].getStatus().equals("CANCELLED")) count++;
        }
        return count;
    }

    @Override
    public void updateContact(String email, String phone) {
        super.updateContact(email, phone);
    }

    @Override
    public String getRolePermissions() {
        return "Manage consultation hours, blackout dates and daily roster";
    }

    public String getProviderNumber() { return providerNumber; }
    public void setProviderNumber(String value) { providerNumber = value; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String value) { specialty = value; }
    public String getClinicLocation() { return clinicLocation; }
    public void setClinicLocation(String value) { clinicLocation = value; }
    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double value) { if (value >= 0) consultationFee = value; }
    public String getWeeklyHours() { return weeklyHours; }
    public String getBlackoutDate() { return blackoutDate; }
    public void setBlackoutDate(String value) { blackoutDate = value; }
}
