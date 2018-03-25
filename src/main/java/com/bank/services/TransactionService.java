package com.bank.services;

import com.bank.domain.Transaction;
import com.bank.services.exceptions.TransactionExecutionException;

import javax.ws.rs.NotFoundException;
import java.util.List;

public interface TransactionService {

    Transaction getTransaction(long transactionId);
    List<Transaction> getTransactions();
    Transaction executeTransaction(Transaction transaction) throws TransactionExecutionException;

}
