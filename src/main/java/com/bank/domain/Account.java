package com.bank.domain;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "accounts")
@NamedQueries(
        {
                @NamedQuery(
                        name = "com.bank.domain.Account.getAll",
                        query = "SELECT a FROM Account a"
                )
        }
)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty
    private String holder;

    @Valid
    @NotNull
    @Embedded
    private Money balance;

    @NotEmpty
    private String type;

    public Account(){
        // For Jackson
    }

    public Account(String holder, Money balance, String type) {
        this.holder = holder;
        this.balance = balance;
        this.type = type;
    }

    public void withdraw(double amount) {
        balance = new Money(balance.getCurrency(), balance.getValue() - amount);
    }

    public void deposit(double amount) {
        balance = new Money(balance.getCurrency(), balance.getValue() + amount);
    }

    public long getId() {
        return id;
    }

    public String getHolder() {
        return holder;
    }

    public Money getBalance() {
        return balance;
    }

    public String getType() {
        return type;
    }

}
