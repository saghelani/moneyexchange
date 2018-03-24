package com.bank.domain;

public class Transaction {

    private long id;
    private long remitterAccountId;
    private long beneficiaryAccountId;
    private Amount amount;
    private TransactionStatus status;

    public Transaction(long id, long remitterAccountId, long beneficiaryAccountId) {
        this.id = id;
        this.remitterAccountId = remitterAccountId;
        this.beneficiaryAccountId = beneficiaryAccountId;
    }
}
