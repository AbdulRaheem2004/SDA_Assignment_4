package hospital.model;

import hospital.service.AuditLog;
import hospital.service.ReportService;

public class HospitalAdministrator extends User {
    private String adminId;
    private int accessLevel;
    private String assignedClinicId;
    private String reportFormat;

    public HospitalAdministrator(String id, String name, String adminId, String clinicId) {
        super(id, name, "admin@demo.local", "03000000001", "demo123");
        this.adminId = adminId;
        this.accessLevel = 2;
        this.assignedClinicId = clinicId;
        this.reportFormat = "TEXT";
    }

    public String requestUtilizationReport(ReportService reports) {
        return reports.generateUtilizationReport();
    }

    public String reviewAuditLog(AuditLog log, String filter) {
        return log.query(filter);
    }

    public boolean updateClinicPolicy(int noticeHours, double lateFeePercent) {
        return noticeHours >= 0 && lateFeePercent >= 0 && lateFeePercent <= 100;
    }

    @Override
    public String getRolePermissions() { return "View reports, audit events and clinic policies"; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String value) { adminId = value; }
    public int getAccessLevel() { return accessLevel; }
    public void setAccessLevel(int value) { accessLevel = value; }
    public String getAssignedClinicId() { return assignedClinicId; }
    public void setAssignedClinicId(String value) { assignedClinicId = value; }
    public String getReportFormat() { return reportFormat; }
    public void setReportFormat(String value) { reportFormat = value; }
}
