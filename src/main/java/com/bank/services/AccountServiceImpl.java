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

    /**
     * This method returns an account based on the accountId
     * @param accountId
     * @return Account
     * @throws NotFoundException
     */
    @Override
    public Account getAccount(long accountId) {
        Account account = accountDao.get(accountId);
        if (account != null) {
            return account;
        }
        throw new NotFoundException(String.format("Account %s not found", accountId));
    }

    /**
     * This method returns all available accounts
     * @return List<Account>
     */
    @Override
    public List<Account> getAccounts() {
        return accountDao.getAll();
    }

    /**
     * This method creates an account if it does not exist otherwise it updates it
     * @param account
     * @return Account
     */
    @Override
    public Account createOrUpdateAccount(Account account) {
        return accountDao.saveOrUpdate(account);
    }

    /**
     * This method deletes an account by id
     * @param accountId
     * @throws NotFoundException when the account does not exist
     */
    @Override
    public void deleteAccount(long accountId) {
        accountDao.delete(accountId);;
    }

}
