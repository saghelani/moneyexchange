package com.bank.services;

import com.bank.domain.Account;

import javax.ws.rs.NotFoundException;
import java.util.List;

public interface AccountService {

    Account getAccount(long accountId) throws NotFoundException;
    List<Account> getAccounts();
    Account createOrUpdateAccount(Account account);
    void deleteAccount(long accountId) throws NotFoundException;

}
