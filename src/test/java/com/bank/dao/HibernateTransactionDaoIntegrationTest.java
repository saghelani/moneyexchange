package com.bank.dao;

import com.bank.domain.Transaction;
import com.bank.domain.Money;
import com.bank.domain.Transaction;
import io.dropwizard.testing.junit.DAOTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.validation.ConstraintViolationException;
import java.util.Currency;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class HibernateTransactionDaoIntegrationTest {

    @Rule
    public DAOTestRule database = DAOTestRule.newBuilder().addEntityClass(Transaction.class).build();

    private TransactionDao transactionDao;

    @Before
    public void setUp() {
        transactionDao = new HibernateTransactionDao(database.getSessionFactory());
    }

    @Test
    public void testCreatesValidTransaction() {
        Transaction transaction = new Transaction(123, 234, new Money(Currency.getInstance("GBP"), 10));
        Transaction result = database.inTransaction(() -> transactionDao.saveOrUpdate(transaction));
        assertThat(result.getId(), notNullValue());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testFailsToCreateTransactionWhenAmountNull() {
        Transaction transaction = new Transaction(123, 234, null);
        database.inTransaction(() -> transactionDao.saveOrUpdate(transaction));
    }

    @Test(expected = ConstraintViolationException.class)
    public void testFailsToCreateTransactionWhenAmountHasNoCurrency() {
        Transaction transaction = new Transaction(123, 234, new Money(null, 10));
        database.inTransaction(() -> transactionDao.saveOrUpdate(transaction));
    }

    @Test(expected = ConstraintViolationException.class)
    public void testFailsToCreateTransactionWhenAmountNegative() {
        Transaction transaction = new Transaction(123, 234, new Money(Currency.getInstance("GBP"), -5));
        database.inTransaction(() -> transactionDao.saveOrUpdate(transaction));
    }

    @Test
    public void testGetTransactionByValidId() {
        Transaction transaction = new Transaction(123, 234, new Money(Currency.getInstance("GBP"), 10));
        long id = database.inTransaction(() -> transactionDao.saveOrUpdate(transaction).getId());

        Transaction result = transactionDao.get(id);

        assertThat(result.getId(), equalTo(id));
        assertThat(result.getRemitterAccountId(), equalTo(123L));
        assertThat(result.getBeneficiaryAccountId(), equalTo(234L));
        assertThat(result.getAmount().getValue(), equalTo(10.0));
        assertThat(result.getStatus(), is(nullValue()));
    }

    @Test
    public void testGetTransactionByInvalidId() {
        Transaction result = transactionDao.get(123);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void testGetAllTransactions() {
        Transaction transaction = new Transaction(123, 234, new Money(Currency.getInstance("GBP"), 10));
        Transaction transaction2 = new Transaction(123, 234, new Money(Currency.getInstance("GBP"), 10));
        database.inTransaction(() -> {
            transactionDao.saveOrUpdate(transaction);
            transactionDao.saveOrUpdate(transaction2);
        });

        List<Transaction> result = transactionDao.getAll();

        assertThat(result.size(), equalTo(2));
    }

}
