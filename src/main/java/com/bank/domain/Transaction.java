package com.bank.domain;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Currency;
import java.util.Objects;

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

    private TransactionStatus status;

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

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj)
//            return true;
//        if (obj == null || getClass() != obj.getClass())
//            return false;
//        Transaction otherTransaction = (Transaction) obj;
//        return Objects.equals(id, otherTransaction.id);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hashCode(id);
//    }
}
