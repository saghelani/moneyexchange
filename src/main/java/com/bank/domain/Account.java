package com.bank.domain;

public class Account {

    private long id;
    private String holder;
    private Amount balance;
    private String type;

    public Account(int id, String holder, Amount balance, String type) {
        this.id = id;
        this.holder = holder;
        this.balance = balance;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getHolder() {
        return holder;
    }

    public Amount getBalance() {
        return balance;
    }

    public String getType() {
        return type;
    }
}
