package com.bank.dao;

import com.bank.domain.Transaction;

import java.util.List;

public interface TransactionDao {

    Transaction get(long id);
    List<Transaction> getAll();
    Transaction saveOrUpdate(Transaction transaction);

}
