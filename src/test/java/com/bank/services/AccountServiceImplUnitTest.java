package com.bank.services;

import com.bank.dao.AccountDao;
import com.bank.domain.Account;
import com.bank.domain.Money;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.NotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;

public class AccountServiceImplUnitTest {

    AccountService accountService;
    AccountDao accountDaoMock;

    @Before
    public void beforeEachTest() {
        accountDaoMock = Mockito.mock(AccountDao.class);
        accountService = new AccountServiceImpl(accountDaoMock);
    }

    @Test
    public void testGetAccountByValidId() {
        Account expected = new Account("John", new Money(), "current");
        Mockito.when(accountDaoMock.get(any(Long.class))).thenReturn(expected);

        Account result = accountService.getAccount(123);

        assertThat(result.getHolder(), equalTo("John"));
        Mockito.verify(accountDaoMock, Mockito.times(1)).get(any(Long.class));
    }

    @Test(expected = NotFoundException.class)
    public void testGetAccountByInvalidId() {
        Mockito.when(accountDaoMock.get(any(Long.class))).thenReturn(null);

        accountService.getAccount(123);

        Mockito.verify(accountDaoMock, Mockito.times(1)).get(any(Long.class));
    }

    @Test
    public void testGetAllAccounts() {
        List<Account> expected = new ArrayList<>();
        expected.add(new Account("John", new Money(), "current"));
        expected.add(new Account("John2", new Money(), "current2"));
        Mockito.when(accountDaoMock.getAll()).thenReturn(expected);

        List<Account> result = accountService.getAccounts();

        assertThat(result.size(), equalTo(2));
        Mockito.verify(accountDaoMock, Mockito.times(1)).getAll();
    }

    @Test
    public void testSaveOrUpdateAccount() {
        Account expected = new Account("John", new Money(), "current");

        accountService.createOrUpdateAccount(expected);

        Mockito.verify(accountDaoMock, Mockito.times(1)).saveOrUpdate(any(Account.class));
    }

    @Test
    public void testDeleteAccount() {
        accountService.deleteAccount(123);

        Mockito.verify(accountDaoMock, Mockito.times(1)).delete(any(Long.class));
    }

}
