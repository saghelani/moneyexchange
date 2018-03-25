package com.bank.services;

import com.bank.dao.TransactionDao;
import com.bank.domain.Account;
import com.bank.domain.Transaction;
import com.bank.domain.Money;
import com.bank.domain.TransactionStatus;
import com.bank.services.exceptions.TransactionExecutionException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static com.sun.javaws.JnlpxArgs.verify;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;

public class TransactionServiceImplTest {

    TransactionService transactionService;
    AccountService accountServiceMock;
    TransactionDao transactionDaoMock;

    @Before
    public void beforeEachTest() {
        transactionDaoMock = Mockito.mock(TransactionDao.class);
        accountServiceMock = Mockito.mock(AccountService.class);
        transactionService = new TransactionServiceImpl(transactionDaoMock, accountServiceMock);
    }

    @Test
    public void testGetTransactionByValidId() {
        Transaction expected = new Transaction(123, 234, new Money());
        Mockito.when(transactionDaoMock.get(any(Long.class))).thenReturn(expected);

        Transaction result = transactionService.getTransaction(123);

        assertThat(result.getRemitterAccountId(), equalTo(123L));
        Mockito.verify(transactionDaoMock, Mockito.times(1)).get(any(Long.class));
    }

    @Test(expected = NotFoundException.class)
    public void testGetTransactionByInvalidId() {
        Mockito.when(transactionDaoMock.get(any(Long.class))).thenReturn(null);
        transactionService.getTransaction(123);

        Mockito.verify(transactionDaoMock, Mockito.times(1)).get(any(Long.class));
    }

    @Test
    public void testGetAllTransactions() {
        List<Transaction> expected = new ArrayList<>();
        expected.add(new Transaction(123, 234, new Money()));
        expected.add(new Transaction(123, 234, new Money()));
        Mockito.when(transactionDaoMock.getAll()).thenReturn(expected);

        List<Transaction> result = transactionService.getTransactions();

        assertThat(result.size(), equalTo(2));
        Mockito.verify(transactionDaoMock, Mockito.times(1)).getAll();
    }

    @Test
    public void testExecuteValidTransaction() {
        Transaction expected = new Transaction(123, 234, new Money(Currency.getInstance("GBP"), 10));
        Account account = new Account("John", new Money(Currency.getInstance("GBP"), 100), "current");
        Account account2 = new Account("John", new Money(Currency.getInstance("GBP"), 50), "current");
        Mockito.when(accountServiceMock.getAccount(123)).thenReturn(account);
        Mockito.when(accountServiceMock.getAccount(234)).thenReturn(account2);

        Transaction result = transactionService.executeTransaction(expected);

        Mockito.verify(transactionDaoMock, Mockito.times(1)).saveOrUpdate(any(Transaction.class));
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        Mockito.verify(accountServiceMock, Mockito.times(2)).createOrUpdateAccount(captor.capture());
        List<Account> savedAccount = captor.getAllValues();

        assertThat(result.getStatus(), equalTo(TransactionStatus.EXECUTED));
        assertThat(savedAccount.get(0).getBalance().getValue(), equalTo(90.0));
        assertThat(savedAccount.get(1).getBalance().getValue(), equalTo(60.0));
    }

    @Test(expected = TransactionExecutionException.class)
    public void testExecuteTransactionWhenAccountHasInsufficientFunds() {
        Transaction expected = new Transaction(123, 234, new Money(Currency.getInstance("GBP"), 10));
        Account account = new Account("John", new Money(Currency.getInstance("GBP"), 5), "current");
        Account account2 = new Account("John", new Money(Currency.getInstance("GBP"), 50), "current");
        Mockito.when(accountServiceMock.getAccount(123)).thenReturn(account);
        Mockito.when(accountServiceMock.getAccount(234)).thenReturn(account2);

        try {
            transactionService.executeTransaction(expected);
        } catch (TransactionExecutionException e) {
            ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
            Mockito.verify(transactionDaoMock, Mockito.times(1)).saveOrUpdate(captor.capture());
            Transaction savedTransaction = captor.getValue();
            assertThat(savedTransaction.getStatus(), equalTo(TransactionStatus.FAILED));
            throw e;
        }
    }

    @Test(expected = TransactionExecutionException.class)
    public void testExecuteTransactionWhenAccountInvalid() {
        Transaction expected = new Transaction(123, 234, new Money(Currency.getInstance("GBP"), 10));
        Mockito.when(accountServiceMock.getAccount(123)).thenThrow(NotFoundException.class);

        try {
            transactionService.executeTransaction(expected);
        } catch (TransactionExecutionException e) {
            ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
            Mockito.verify(transactionDaoMock, Mockito.times(1)).saveOrUpdate(captor.capture());
            Transaction savedTransaction = captor.getValue();
            assertThat(savedTransaction.getStatus(), equalTo(TransactionStatus.FAILED));
            throw e;
        }
    }

}
