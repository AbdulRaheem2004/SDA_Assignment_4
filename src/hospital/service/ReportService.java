package hospital.service;

import hospital.model.Appointment;
import hospital.model.Doctor;

public class ReportService {
    private String reportFormat;
    private String reportTimezone;
    private int cacheDurationSeconds;
    private int maxRows;
    private ApptDB database;
    private Doctor[] doctors;

    public ReportService(ApptDB database, Doctor[] doctors) {
        this.reportFormat = "TEXT";
        this.reportTimezone = "Asia/Karachi";
        this.cacheDurationSeconds = 0;
        this.maxRows = 30;
        this.database = database;
        this.doctors = doctors;
    }

    public String generateUtilizationReport() {
        Appointment[] appointments = database.getAppointments();
        int confirmed = 0;
        int cancelled = 0;
        double fees = 0;
        for (int i = 0; i < appointments.length; i++) {
            if (appointments[i] != null) {
                if (appointments[i].getStatus().equals("CANCELLED")) cancelled++;
                else {
                    confirmed++;
                    fees += appointments[i].getDepositAmount();
                }
            }
        }
        int average = confirmed == 0 ? 0 : (int) (fees / confirmed);
        String result = "Appointments active: " + confirmed + " | Cancelled: " + cancelled
                + " | Active booking total PKR " + fees + " | Avg fee (int cast) PKR " + average + "\n";
        for (int i = 0; i < doctors.length; i++) {
            if (doctors[i] != null) {
                result += doctors[i].getFullName() + " : "
                        + calculateDoctorWorkload(doctors[i].getUserId(), appointments) + " appointments\n";
            }
        }
        return result;
    }

    public int calculateDoctorWorkload(String doctorId, Appointment[] appointments) {
        int count = 0;
        for (int i = 0; i < appointments.length; i++) {
            if (appointments[i] != null && appointments[i].getDoctorId().equals(doctorId)
                    && !appointments[i].getStatus().equals("CANCELLED")) count++;
        }
        return count;
    }

    public String exportReport(String reportData, String format) {
        return format.equalsIgnoreCase("TEXT") ? reportData : "Only text reports are supported";
    }

    public String getReportFormat() { return reportFormat; }
    public void setReportFormat(String value) { reportFormat = value; }
    public String getReportTimezone() { return reportTimezone; }
    public void setReportTimezone(String value) { reportTimezone = value; }
    public int getCacheDurationSeconds() { return cacheDurationSeconds; }
    public void setCacheDurationSeconds(int value) { cacheDurationSeconds = value; }
    public int getMaxRows() { return maxRows; }
    public void setMaxRows(int value) { maxRows = value; }
}
