package hospital.model;

public class Patient extends User {
    private String patientNumber;
    private String dateOfBirth;
    private String emergencyContact;
    private boolean notificationOptIn;

    public Patient(String userId, String name, String email, String phone, String patientNumber) {
        super(userId, name, email, phone, "demo123");
        this.patientNumber = patientNumber;
        this.dateOfBirth = "Not provided";
        this.emergencyContact = "Not provided";
        this.notificationOptIn = true;
    }

    public Patient(String userId, String name, String email, String phone,
                   String patientNumber, String dateOfBirth, String emergencyContact) {
        this(userId, name, email, phone, patientNumber);
        this.dateOfBirth = dateOfBirth;
        this.emergencyContact = emergencyContact;
    }

    public Doctor[] searchDoctors(Doctor[] doctors, String specialty, String location) {
        Doctor[] matches = new Doctor[doctors.length];
        int count = 0;
        for (int i = 0; i < doctors.length; i++) {
            if (doctors[i] != null
                    && doctors[i].getSpecialty().toLowerCase().contains(specialty.toLowerCase())
                    && doctors[i].getClinicLocation().toLowerCase().contains(location.toLowerCase())) {
                matches[count] = doctors[i];
                count++;
            }
        }
        return matches;
    }

    public boolean selectAppointmentSlot(TimeSlot slot) {
        return slot != null && slot.isAvailable();
    }

    @Override
    public String getRolePermissions() {
        return "Search, book, view, cancel and reschedule appointments";
    }

    public String getPatientNumber() { return patientNumber; }
    public void setPatientNumber(String value) { patientNumber = value; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String value) { dateOfBirth = value; }
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String value) { emergencyContact = value; }
    public boolean isNotificationOptIn() { return notificationOptIn; }
    public void setNotificationOptIn(boolean value) { notificationOptIn = value; }
}
