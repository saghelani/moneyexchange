package com.bank.services;

import com.bank.dao.AccountDao;
import com.bank.domain.Account;

import javax.ws.rs.NotFoundException;
import java.util.List;

public class AccountServiceImpl implements AccountService {

    private AccountDao accountDao;

    public AccountServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public Account getAccount(long accountId) throws NotFoundException {
        Account account = accountDao.get(accountId);
        if (account != null) {
            return account;
        }
        throw new NotFoundException(String.format("Account %s not found", accountId));
    }

    @Override
    public List<Account> getAccounts() {
        return accountDao.getAll();
    }

    @Override
    public Account createOrUpdateAccount(Account account) {
        return accountDao.saveOrUpdate(account);
    }

    @Override
    public void deleteAccount(long accountId) {
        accountDao.delete(accountId);
    }

}
