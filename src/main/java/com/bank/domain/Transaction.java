package com.bank.domain;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "transactions")
@NamedQueries(
        {
                @NamedQuery(
                        name = "com.bank.domain.Transaction.getAll",
                        query = "SELECT t FROM Transaction t"
                )
        }
)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long remitterAccountId;

    private long beneficiaryAccountId;

    @Valid
    @NotNull
    @Embedded
    private Money amount;

    @Embedded
    private TransactionResult result;

    public Transaction() {
        // For Jackson
    }

    public Transaction(long remitterAccountId, long beneficiaryAccountId, Money amount) {
        this.remitterAccountId = remitterAccountId;
        this.beneficiaryAccountId = beneficiaryAccountId;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public Money getAmount() {
        return amount;
    }

    public long getRemitterAccountId() {
        return remitterAccountId;
    }

    public long getBeneficiaryAccountId() {
        return beneficiaryAccountId;
    }

    public TransactionResult getTransactionResult() {
        return result;
    }

    public void setTransactionResult(TransactionResult result) {
        this.result = result;
    }

}
