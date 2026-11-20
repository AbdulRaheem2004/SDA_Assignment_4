package hospital.service;

import hospital.model.Appointment;

public class SMSNotification extends NotificationChannel implements NotificationSender {
    private String senderId;
    private String regionPrefix;
    private int maxCharacters;

    public SMSNotification(String senderId, String regionPrefix) {
        super("SMS", "CONSOLE_ONLY", 5);
        this.senderId = senderId;
        this.regionPrefix = regionPrefix;
        this.maxCharacters = 160;
    }

    @Override
    public boolean send(String recipient, String subject, String body) {
        if (!isEnabled() || recipient.length() == 0) return false;
        System.out.println("[SMS simulation to " + recipient + "] " + body);
        return true;
    }

    @Override
    public String formatConfirmation(Appointment appointment) {
        return "Booking " + appointment.getAppointmentId() + " confirmed; QR " + appointment.getQrToken();
    }

    @Override
    public String formatCancellation(Appointment appointment) {
        return "Booking " + appointment.getAppointmentId() + " cancelled.";
    }

    public String getSenderId() { return senderId; }
    public void setSenderId(String value) { senderId = value; }
    public String getRegionPrefix() { return regionPrefix; }
    public void setRegionPrefix(String value) { regionPrefix = value; }
    public int getMaxCharacters() { return maxCharacters; }
    public void setMaxCharacters(int value) { maxCharacters = value; }
}
