package com.bank.services;

import com.bank.domain.Transaction;

import java.util.List;

public interface TransactionService {

    Transaction getTransaction(long transactionId);
    List<Transaction> getTransactions();
    Transaction executeTransaction(Transaction transaction);

}
