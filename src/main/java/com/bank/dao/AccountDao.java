package com.bank.dao;

import com.bank.domain.Account;

import java.util.List;

public interface AccountDao {

    Account get(long id);
    List<Account> getAll();
    Account saveOrUpdate(Account account);
    void delete(long id);

}
