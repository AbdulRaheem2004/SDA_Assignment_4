package hospital.service;

public interface NotificationSender {
    boolean send(String recipient, String subject, String body);
}
