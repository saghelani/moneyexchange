package com.bank.domain;

public class TransactionResult {

    private TransactionStatus transactionStatus;
    private String reason;

    public TransactionResult() {
        // For Jackson
    }

    public TransactionResult(TransactionStatus transactionStatus, String reason) {
        this.transactionStatus = transactionStatus;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }
}
