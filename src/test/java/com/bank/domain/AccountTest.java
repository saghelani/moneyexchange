package com.bank.domain;

import org.junit.Test;

import java.util.Currency;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class AccountTest {

    @Test
    public void correctlyUpdatesBalanceOnDeposit() {
        Account account = new Account("", new Money(Currency.getInstance("GBP"), 15), "");
        account.deposit(10);
        assertThat(account.getBalance().getValue(), equalTo(25.0));
    }

    @Test
    public void correctlyUpdatesBalanceOnWithdrawal() {
        Account account = new Account("", new Money(Currency.getInstance("GBP"), 15), "");
        account.withdraw(10);
        assertThat(account.getBalance().getValue(), equalTo(5.0));
    }
}
