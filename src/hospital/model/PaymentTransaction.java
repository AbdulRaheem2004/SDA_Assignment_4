package hospital.model;

public class PaymentTransaction {
    private String transactionId;
    private String appointmentId;
    private double amount;
    private String currency;
    private String status;
    private String gatewayReference;
    private double refundedAmount;

    public PaymentTransaction(String transactionId, String appointmentId, double amount) {
        this.transactionId = transactionId;
        this.appointmentId = appointmentId;
        this.amount = amount;
        this.currency = "PKR";
        this.status = "PENDING";
        this.gatewayReference = "";
        this.refundedAmount = 0;
    }

    public void markAuthorized(String reference) {
        status = "AUTHORIZED";
        gatewayReference = reference;
    }

    public void markDeclined(String reason) {
        status = "DECLINED";
        gatewayReference = reason;
    }

    public double calculateRefund(double refundPercent) {
        if (refundPercent < 0 || refundPercent > 100) return 0;
        return amount * refundPercent / 100.0;
    }

    public boolean isSuccessful() {
        return status.equals("AUTHORIZED") || status.equals("PARTIALLY_REFUNDED")
                || status.equals("REFUNDED");
    }

    public boolean applyRefund(double refund) {
        if (!isSuccessful() || refund < 0 || refundedAmount + refund > amount) return false;
        refundedAmount += refund;
        status = refundedAmount >= amount ? "REFUNDED" : "PARTIALLY_REFUNDED";
        return true;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String value) { transactionId = value; }
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String value) { appointmentId = value; }
    public double getAmount() { return amount; }
    public void setAmount(double value) { amount = value; }
    public String getCurrency() { return currency; }
    public void setCurrency(String value) { currency = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
    public String getGatewayReference() { return gatewayReference; }
    public void setGatewayReference(String value) { gatewayReference = value; }
    public double getRefundedAmount() { return refundedAmount; }
    public void setRefundedAmount(double value) { refundedAmount = value; }
}
