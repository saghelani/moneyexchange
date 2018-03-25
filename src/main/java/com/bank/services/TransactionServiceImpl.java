package com.bank.services;

import com.bank.dao.TransactionDao;
import com.bank.domain.Account;
import com.bank.domain.Transaction;
import com.bank.domain.TransactionStatus;
import com.bank.services.exceptions.TransactionExecutionException;

import javax.ws.rs.NotFoundException;
import java.util.List;

public class TransactionServiceImpl implements TransactionService {

    private TransactionDao transactionDao;
    private AccountService accountService;

    public TransactionServiceImpl(TransactionDao transactionDao, AccountService accountService) {
        this.transactionDao = transactionDao;
        this.accountService = accountService;
    }

    @Override
    public Transaction getTransaction(long transactionId) {
        Transaction transaction = transactionDao.get(transactionId);
        if (transaction != null) {
            return transaction;
        }
        throw new NotFoundException(String.format("Transaction %s not found", transactionId));
    }

    @Override
    public List<Transaction> getTransactions() {
        return transactionDao.getAll();
    }

    @Override
    public Transaction executeTransaction(Transaction transaction) throws TransactionExecutionException {
        Account remitterAccount;
        Account beneficiaryAccount;

        try {
            remitterAccount = accountService.getAccount(transaction.getRemitterAccountId());
            beneficiaryAccount = accountService.getAccount(transaction.getBeneficiaryAccountId());
        } catch (NotFoundException e) {
            saveTransaction(transaction, TransactionStatus.FAILED);
            throw new TransactionExecutionException("Account not found");
        }

        if (remitterAccount.getBalance().getValue() < transaction.getAmount().getValue()) {
            saveTransaction(transaction, TransactionStatus.FAILED);
            throw new TransactionExecutionException("Insufficient funds in account");
        }

        remitterAccount.withdraw(transaction.getAmount().getValue());
        beneficiaryAccount.deposit(transaction.getAmount().getValue());

        accountService.createOrUpdateAccount(remitterAccount);
        accountService.createOrUpdateAccount(beneficiaryAccount);

        saveTransaction(transaction, TransactionStatus.EXECUTED);
        return transaction;
    }

    private void saveTransaction(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
        transactionDao.saveOrUpdate(transaction);
    }

}
