package com.bank.services;

import com.bank.dao.TransactionDao;
import com.bank.domain.Account;
import com.bank.domain.Transaction;
import com.bank.domain.TransactionResult;
import com.bank.domain.TransactionStatus;

import javax.ws.rs.NotFoundException;
import java.util.List;

public class TransactionServiceImpl implements TransactionService {

    private TransactionDao transactionDao;
    private AccountService accountService;

    public TransactionServiceImpl(TransactionDao transactionDao, AccountService accountService) {
        this.transactionDao = transactionDao;
        this.accountService = accountService;
    }

    /**
     * This method returns a transaction based on the transactionId
     * @param transactionId Id of the transaction to get
     * @return the transaction
     * @throws NotFoundException when the transaction does not exist
     */
    @Override
    public Transaction getTransaction(long transactionId) {
        Transaction transaction = transactionDao.get(transactionId);
        if (transaction != null) {
            return transaction;
        }
        throw new NotFoundException(String.format("Transaction %s not found", transactionId));
    }

    /**
     * This method returns all transactions available
     * @return a list of transactions
     */
    @Override
    public List<Transaction> getTransactions() {
        return transactionDao.getAll();
    }

    /**
     * This method executes the transaction defined in the input parameter
     * @param transaction to execute
     * @return the executed transaction
     */
    @Override
    public Transaction executeTransaction(Transaction transaction) {
        Account remitterAccount;
        Account beneficiaryAccount;

        try {
            remitterAccount = accountService.getAccount(transaction.getRemitterAccountId());
            beneficiaryAccount = accountService.getAccount(transaction.getBeneficiaryAccountId());
        } catch (NotFoundException e) {
            saveTransaction(transaction, TransactionStatus.FAILED, "Account not found");
            return transaction;
        }

        if (remitterAccount.getBalance().getValue() < transaction.getAmount().getValue()) {
            saveTransaction(transaction, TransactionStatus.FAILED, "Insufficient funds in account");
            return transaction;
        }

        if (remitterAccount.getBalance().getCurrency() != transaction.getAmount().getCurrency() ||
                beneficiaryAccount.getBalance().getCurrency() != transaction.getAmount().getCurrency()) {
            saveTransaction(transaction, TransactionStatus.FAILED, "Transaction in currency not supported by account");
            return transaction;
        }

        remitterAccount.withdraw(transaction.getAmount().getValue());
        beneficiaryAccount.deposit(transaction.getAmount().getValue());

        accountService.createOrUpdateAccount(remitterAccount);
        accountService.createOrUpdateAccount(beneficiaryAccount);

        saveTransaction(transaction, TransactionStatus.EXECUTED, "Successfully executed");
        return transaction;
    }

    private void saveTransaction(Transaction transaction, TransactionStatus status, String reason) {
        transaction.setTransactionResult(new TransactionResult(status, reason));
        transactionDao.saveOrUpdate(transaction);
    }

}
