package com.bank.controllers;

import com.bank.config.NotFoundExceptionMapper;
import com.bank.domain.Account;
import com.bank.domain.Money;
import com.bank.services.AccountService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Currency;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AccountControllerTest {

    private static final AccountService accountServiceMock = mock(AccountService.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AccountController(accountServiceMock))
            .setRegisterDefaultExceptionMappers(false)
            .addProvider(NotFoundExceptionMapper.class).build();

    @After
    public void tearDown(){
        reset(accountServiceMock);
    }

    @Test
    public void testGetValidAccount() {
        Account account = new Account("John", new Money(Currency.getInstance("GBP"), 5), "current");
        when(accountServiceMock.getAccount(any(Long.class))).thenReturn(account);

        Account result = resources.target("/accounts/1").request().get(Account.class);

        assertThat(result.getHolder(), equalTo("John"));
        assertThat(result.getBalance().getValue(), equalTo(5.0));
        verify(accountServiceMock).getAccount(1);
    }

    @Test
    public void testGetInvalidAccount() {
        when(accountServiceMock.getAccount(any(Long.class))).thenThrow(new NotFoundException("Not Found"));
        
        WebApplicationException expectedException = null;
        try {
            resources.target("/accounts/1").request().get(Account.class);
        } catch (WebApplicationException e) {
            expectedException = e;
        }
        assertThat(expectedException, notNullValue());
        assertThat(expectedException.getResponse().getStatus(), equalTo(404));
        assertThat(expectedException.getResponse().getMediaType().toString(), equalTo(MediaType.APPLICATION_JSON));
        verify(accountServiceMock).getAccount(1);
    }

    @Test
    public void testGetAccounts() {
        Response result = resources.target("/accounts").request().get();

        assertThat(result.getStatusInfo(), equalTo(Response.Status.OK));
        verify(accountServiceMock).getAccounts();
    }

    @Test
    public void testDeleteValidAccount() {
        Response result = resources.target("/accounts/1").request().delete();

        assertThat(result.getStatusInfo(), equalTo(Response.Status.NO_CONTENT));
        verify(accountServiceMock).deleteAccount(1);
    }

    @Test
    public void testDeleteInvalidAccount() {
        doThrow(new NotFoundException("Not Found")).when(accountServiceMock).deleteAccount(any(Long.class));

        WebApplicationException expectedException = null;
        try {
            resources.target("/accounts/1").request().delete(Account.class);
        } catch (WebApplicationException e) {
            expectedException = e;
        }
        assertThat(expectedException, notNullValue());
        assertThat(expectedException.getResponse().getStatus(), equalTo(404));
        assertThat(expectedException.getResponse().getMediaType().toString(), equalTo(MediaType.APPLICATION_JSON));
        verify(accountServiceMock).deleteAccount(1);
    }

    @Test
    public void testCreateValidAccount() {
        Account account = new Account("John", new Money(Currency.getInstance("GBP"), 5), "current");
        when(accountServiceMock.createOrUpdateAccount(any(Account.class))).thenReturn(account);

        Response result = resources.target("/accounts").request().post(Entity.entity(account, MediaType.APPLICATION_JSON));

        assertThat(result.getStatusInfo(), equalTo(Response.Status.CREATED));
        verify(accountServiceMock).createOrUpdateAccount(any(Account.class));
    }

    @Test
    public void testFailsToCreateAccountWhenHolderEmpty() {
        Account account = new Account("", new Money(Currency.getInstance("GBP"), 5), "current");

        Response result = resources.target("/accounts").request().post(Entity.entity(account, MediaType.APPLICATION_JSON));

        assertThat(result.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
        verify(accountServiceMock, times(0)).createOrUpdateAccount(any(Account.class));
    }

    @Test
    public void testFailsToCreateAccountWhenTypeEmpty() {
        Account account = new Account("John", new Money(Currency.getInstance("GBP"), 5), "");

        Response result = resources.target("/accounts").request().post(Entity.entity(account, MediaType.APPLICATION_JSON));

        assertThat(result.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
        verify(accountServiceMock, times(0)).createOrUpdateAccount(any(Account.class));
    }

    @Test
    public void testFailsToCreateAccountWhenBalanceNull() {
        Account account = new Account("John", null, "current");

        Response result = resources.target("/accounts").request().post(Entity.entity(account, MediaType.APPLICATION_JSON));

        assertThat(result.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
        verify(accountServiceMock, times(0)).createOrUpdateAccount(any(Account.class));
    }

    @Test
    public void testFailsToCreateAccountWhenBalanceCurrencyNull() {
        Account account = new Account("John", new Money(null, 5), "current");

        Response result = resources.target("/accounts").request().post(Entity.entity(account, MediaType.APPLICATION_JSON));

        assertThat(result.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
        verify(accountServiceMock, times(0)).createOrUpdateAccount(any(Account.class));
    }

    @Test
    public void testFailsToCreateAccountWhenBalanceNegative() {
        Account account = new Account("John", new Money(Currency.getInstance("GBP"), -5), "current");

        Response result = resources.target("/accounts").request().post(Entity.entity(account, MediaType.APPLICATION_JSON));

        assertThat(result.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
        verify(accountServiceMock, times(0)).createOrUpdateAccount(any(Account.class));
    }

}
