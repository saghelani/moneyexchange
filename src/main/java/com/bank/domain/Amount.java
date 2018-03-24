package com.bank.domain;

import java.util.Currency;

public class Amount {

    private Currency currency;
    private Double value;

    public Amount(Currency currency, Double value) {
        this.currency = currency;
        this.value = value;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Double getValue() {
        return value;
    }
}
