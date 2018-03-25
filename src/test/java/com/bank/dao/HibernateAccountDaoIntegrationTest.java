package com.bank.dao;

import com.bank.domain.Account;
import com.bank.domain.Money;
import io.dropwizard.testing.junit.DAOTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.validation.ConstraintViolationException;
import java.util.Currency;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class HibernateAccountDaoIntegrationTest {

    @Rule
    public DAOTestRule database = DAOTestRule.newBuilder().addEntityClass(Account.class).build();

    private AccountDao accountDao;

    @Before
    public void setUp() {
        accountDao = new HibernateAccountDao(database.getSessionFactory());
    }

    @Test
    public void testCreatesValidAccount() {
        Account account = new Account("John", new Money(Currency.getInstance("GBP"), 5), "current");
        Account result = database.inTransaction(() -> accountDao.saveOrUpdate(account));
        assertThat(result.getId(), notNullValue());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testFailtsToCreateAccountWhenHolderEmpty() {
        Account account = new Account("", new Money(Currency.getInstance("GBP"), 5), "current");
        database.inTransaction(() -> accountDao.saveOrUpdate(account));
    }

    @Test(expected = ConstraintViolationException.class)
    public void testFailtsToCreateAccountWhenTypeEmpty() {
        Account account = new Account("John", new Money(Currency.getInstance("GBP"), 5), "");
        database.inTransaction(() -> accountDao.saveOrUpdate(account));
    }

    @Test(expected = ConstraintViolationException.class)
    public void testFailtsToCreateAccountWhenBalanceNull() {
        Account account = new Account("John", null, "current");
        database.inTransaction(() -> accountDao.saveOrUpdate(account));
    }

    @Test(expected = ConstraintViolationException.class)
    public void testFailtsToCreateAccountWhenBalanceHasNoCurrency() {
        Account account = new Account("John", new Money(null, 5), "current");
        database.inTransaction(() -> accountDao.saveOrUpdate(account));
    }

    @Test(expected = ConstraintViolationException.class)
    public void testFailtsToCreateAccountWhenBalanceNegative() {
        Account account = new Account("John", new Money(Currency.getInstance("GBP"), -5), "current");
        database.inTransaction(() -> accountDao.saveOrUpdate(account));
    }

    @Test
    public void testGetAccountByValidId() {
        Account account = new Account("John", new Money(Currency.getInstance("GBP"), 5), "current");
        long id = database.inTransaction(() -> accountDao.saveOrUpdate(account).getId());

        Account result = accountDao.get(id);

        assertThat(result.getId(), equalTo(id));
        assertThat(result.getHolder(), equalTo("John"));
        assertThat(result.getBalance().getValue(), equalTo(5.0));
    }

    @Test
    public void testGetAccountByInvalidId() {
        Account result = accountDao.get(123);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void testGetAllAccounts() {
        Account account = new Account("John", new Money(Currency.getInstance("GBP"), 5), "current");
        Account account2 = new Account("John", new Money(Currency.getInstance("GBP"), 5), "current");

        database.inTransaction(() -> {
            accountDao.saveOrUpdate(account);
            accountDao.saveOrUpdate(account2);
        });

        List<Account> result = accountDao.getAll();

        assertThat(result.size(), equalTo(2));
    }

    @Test
    public void testDeleteAccountById() {
        Account account = new Account("John", new Money(Currency.getInstance("GBP"), 5), "current");
        Account account2 = new Account("John", new Money(Currency.getInstance("GBP"), 5), "current");
        database.inTransaction(() -> {
            accountDao.saveOrUpdate(account);
            accountDao.saveOrUpdate(account2);
        });

        database.inTransaction(() -> accountDao.delete(1));

        List<Account> result = accountDao.getAll();

        assertThat(result.size(), equalTo(1));
    }

}
