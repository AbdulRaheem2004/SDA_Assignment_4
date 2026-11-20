package hospital.service;

import hospital.model.PaymentTransaction;

public class PaymentService {
    private String gatewayEndpoint;
    private String merchantAccountRef;
    private int timeoutSeconds;
    private String supportedCurrency;
    private PaymentTransaction[] transactions;
    private int transactionCount;

    public PaymentService(String accountRef, int capacity) {
        this.gatewayEndpoint = "CONSOLE_SIMULATION";
        this.merchantAccountRef = accountRef;
        this.timeoutSeconds = 5;
        this.supportedCurrency = "PKR";
        this.transactions = new PaymentTransaction[capacity];
        this.transactionCount = 0;
    }

    public PaymentTransaction authorizeDeposit(String patientId, double amount, String details) {
        if (transactionCount >= transactions.length || patientId.length() == 0 || amount <= 0) return null;
        PaymentTransaction transaction = new PaymentTransaction("TX-" + (transactionCount + 1), "", amount);
        if (details.equalsIgnoreCase("approve")) {
            transaction.markAuthorized("SIM-" + patientId);
        } else {
            transaction.markDeclined("Simulated decline");
        }
        transactions[transactionCount] = transaction;
        transactionCount++;
        return transaction;
    }

    public boolean refund(String transactionId, double amount) {
        PaymentTransaction transaction = findTransaction(transactionId);
        return transaction != null && transaction.applyRefund(amount);
    }

    public boolean verifyTransaction(String transactionId) {
        PaymentTransaction transaction = findTransaction(transactionId);
        return transaction != null && transaction.isSuccessful();
    }

    public String createReceipt(String transactionId) {
        PaymentTransaction transaction = findTransaction(transactionId);
        if (transaction == null || !transaction.isSuccessful()) return "Receipt unavailable";
        return "Receipt " + transactionId + ": PKR " + transaction.getAmount();
    }

    public PaymentTransaction findTransaction(String id) {
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i].getTransactionId().equalsIgnoreCase(id)) return transactions[i];
        }
        return null;
    }

    public String getGatewayEndpoint() { return gatewayEndpoint; }
    public void setGatewayEndpoint(String value) { gatewayEndpoint = value; }
    public String getMerchantAccountRef() { return merchantAccountRef; }
    public void setMerchantAccountRef(String value) { merchantAccountRef = value; }
    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int value) { timeoutSeconds = value; }
    public String getSupportedCurrency() { return supportedCurrency; }
    public void setSupportedCurrency(String value) { supportedCurrency = value; }
    public int getTransactionCount() { return transactionCount; }
}
