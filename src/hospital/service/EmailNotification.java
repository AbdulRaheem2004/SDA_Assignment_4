package hospital.service;

import hospital.model.Appointment;

public class EmailNotification extends NotificationChannel implements NotificationSender {
    private String senderAddress;
    private String replyToAddress;
    private String confirmationTemplateId;
    private boolean deliveryTrackingEnabled;

    public EmailNotification(String senderAddress, String replyToAddress) {
        super("Email", "CONSOLE_ONLY", 5);
        this.senderAddress = senderAddress;
        this.replyToAddress = replyToAddress;
        this.confirmationTemplateId = "booking-v1";
        this.deliveryTrackingEnabled = false;
    }

    @Override
    public boolean send(String recipient, String subject, String body) {
        if (!isEnabled() || recipient.length() == 0) return false;
        System.out.println("[Email simulation to " + recipient + "] " + subject + ": " + body);
        return true;
    }

    @Override
    public String formatConfirmation(Appointment appointment) {
        return "Dear patient, appointment " + appointment.getAppointmentId()
                + " at " + appointment.getScheduledAt() + " is confirmed.";
    }

    @Override
    public String formatCancellation(Appointment appointment) {
        return "Appointment " + appointment.getAppointmentId() + " has been cancelled.";
    }

    public String getSenderAddress() { return senderAddress; }
    public void setSenderAddress(String value) { senderAddress = value; }
    public String getReplyToAddress() { return replyToAddress; }
    public void setReplyToAddress(String value) { replyToAddress = value; }
    public String getConfirmationTemplateId() { return confirmationTemplateId; }
    public void setConfirmationTemplateId(String value) { confirmationTemplateId = value; }
    public boolean isDeliveryTrackingEnabled() { return deliveryTrackingEnabled; }
    public void setDeliveryTrackingEnabled(boolean value) { deliveryTrackingEnabled = value; }
}
