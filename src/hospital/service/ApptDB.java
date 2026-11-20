package hospital.service;

import hospital.model.Appointment;

public class ApptDB {
    private String dataSourceRef;
    private String databaseName;
    private int queryTimeoutMs;
    private Appointment[] appointments;
    private int appointmentCount;

    public ApptDB(String databaseName, int capacity) {
        this.dataSourceRef = "MEMORY";
        this.databaseName = databaseName;
        this.queryTimeoutMs = 1000;
        this.appointments = new Appointment[capacity];
        this.appointmentCount = 0;
    }

    public boolean saveAppointment(Appointment appointment) {
        if (appointment == null || appointmentCount >= appointments.length) return false;
        appointments[appointmentCount] = appointment;
        appointmentCount++;
        return true;
    }

    public boolean confirmBooking(Appointment appointment) {
        return appointment != null && findAppointment(appointment.getAppointmentId()) == appointment
                && appointment.getStatus().equals("CONFIRMED");
    }

    public Appointment findAppointment(String appointmentId) {
        for (int i = 0; i < appointmentCount; i++) {
            if (appointments[i].getAppointmentId().equalsIgnoreCase(appointmentId)) return appointments[i];
        }
        return null;
    }

    public Appointment findByQr(String token) {
        for (int i = 0; i < appointmentCount; i++) {
            if (appointments[i].getQrToken().equalsIgnoreCase(token)) return appointments[i];
        }
        return null;
    }

    public Appointment[] getAppointments() { return appointments; }
    public int getAppointmentCount() { return appointmentCount; }
    public String getDataSourceRef() { return dataSourceRef; }
    public void setDataSourceRef(String value) { dataSourceRef = value; }
    public String getDatabaseName() { return databaseName; }
    public void setDatabaseName(String value) { databaseName = value; }
    public int getQueryTimeoutMs() { return queryTimeoutMs; }
    public void setQueryTimeoutMs(int value) { queryTimeoutMs = value; }
}
