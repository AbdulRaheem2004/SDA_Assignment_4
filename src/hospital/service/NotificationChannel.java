package hospital.service;

import hospital.model.Appointment;

public abstract class NotificationChannel {
    private String channelName;
    private String gatewayEndpoint;
    private int timeoutSeconds;
    private boolean enabled;
    private String templateVersion;

    public NotificationChannel(String channelName, String endpoint, int timeoutSeconds) {
        this.channelName = channelName;
        this.gatewayEndpoint = endpoint;
        this.timeoutSeconds = timeoutSeconds;
        this.enabled = true;
        this.templateVersion = "1";
    }

    public abstract boolean send(String recipient, String subject, String body);
    public abstract String formatConfirmation(Appointment appointment);
    public abstract String formatCancellation(Appointment appointment);

    public String describeChannel() {
        return channelName + (enabled ? " enabled" : " disabled");
    }

    public String getChannelName() { return channelName; }
    public void setChannelName(String value) { channelName = value; }
    public String getGatewayEndpoint() { return gatewayEndpoint; }
    public void setGatewayEndpoint(String value) { gatewayEndpoint = value; }
    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int value) { timeoutSeconds = value; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean value) { enabled = value; }
    public String getTemplateVersion() { return templateVersion; }
    public void setTemplateVersion(String value) { templateVersion = value; }
}
