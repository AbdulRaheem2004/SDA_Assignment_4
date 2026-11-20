package hospital.service;

public class AuditLog {
    private String logId;
    private String actorId;
    private String action;
    private String entityType;
    private String entityId;
    private String outcome;
    private String[] entries;
    private int entryCount;

    public AuditLog(String logId) {
        this.logId = logId;
        this.actorId = "SYSTEM";
        this.action = "INIT";
        this.entityType = "System";
        this.entityId = "DEMO";
        this.outcome = "SUCCESS";
        this.entries = new String[100];
        this.entryCount = 0;
    }

    public boolean record(String actor, String action, String type, String id, String outcome) {
        if (entryCount == entries.length) return false;
        this.actorId = actor;
        this.action = action;
        this.entityType = type;
        this.entityId = id;
        this.outcome = outcome;
        entries[entryCount] = actor + " | " + action + " | " + type + " " + id + " | " + outcome;
        entryCount++;
        return true;
    }

    public String query(String filter) {
        String result = "";
        for (int i = 0; i < entryCount; i++) {
            if (entries[i].toLowerCase().contains(filter.toLowerCase())) result += entries[i] + "\n";
        }
        return result.length() == 0 ? "No matching audit events." : result;
    }

    public String export(String from, String to) {
        return "Audit export " + from + " to " + to + "\n" + query("");
    }

    public String getLogId() { return logId; }
    public void setLogId(String value) { logId = value; }
    public String getActorId() { return actorId; }
    public void setActorId(String value) { actorId = value; }
    public String getAction() { return action; }
    public void setAction(String value) { action = value; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String value) { entityType = value; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String value) { entityId = value; }
    public String getOutcome() { return outcome; }
    public void setOutcome(String value) { outcome = value; }
    public int getEntryCount() { return entryCount; }
}
