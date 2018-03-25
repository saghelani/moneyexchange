package com.bank.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Currency;

public class Money {

    @NotNull
    private Currency currency;

    @Min(0)
    private double value;

    public Money() {
        //For Jackson
    }

    public Money(Currency currency, double value) {
        this.currency = currency;
        this.value = value;
    }

    public Currency getCurrency() {
        return currency;
    }

    public double getValue() {
        return value;
    }

}
