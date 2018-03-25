package com.bank.services;

import com.bank.domain.Account;

import java.util.List;

public interface AccountService {

    Account getAccount(long accountId);
    List<Account> getAccounts();
    Account createOrUpdateAccount(Account account);
    void deleteAccount(long accountId);

}
