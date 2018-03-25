package com.bank.controllers;

import com.bank.domain.Account;
import com.bank.domain.Money;
import com.bank.services.AccountService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.Currency;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AccountControllerTest {

    private static final AccountService accountService = mock(AccountService.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AccountController(accountService))
            .setRegisterDefaultExceptionMappers(true).build();

    @Test
    public void testGetValidAccount() {
        Account account = new Account("John", new Money(Currency.getInstance("GBP"), 5), "current");
        when(accountService.getAccount(any(Long.class))).thenReturn(account);

        Account result = resources.target("/accounts/1").request().get(Account.class);

        assertThat(result.getHolder(), equalTo("John"));
        assertThat(result.getBalance().getValue(), equalTo(5.0));
        verify(accountService).getAccount(1);
    }

    @Test
    public void testGetInvalidAccount() {
        when(accountService.getAccount(any(Long.class))).thenThrow(new NotFoundException("Not Found"));

        try {
            Account result = resources.target("/accounts/1").request().get(Account.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus(), equalTo(404));
            assertThat(e.getResponse().getMediaType().toString(), equalTo(MediaType.APPLICATION_JSON));
        }
    }

    @Test
    public void testDeleteValidAccount() {
        Account account = new Account("John", new Money(Currency.getInstance("GBP"), 5), "current");
        when(accountService.getAccount(any(Long.class))).thenReturn(account);


        try {
            Account result = resources.target("/accounts/1").request().get(Account.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus(), equalTo(404));
            assertThat(e.getResponse().getMediaType().toString(), equalTo(MediaType.APPLICATION_JSON));
        }
    }

    @Test
    public void testDeleteInvalidAccount() {
        doThrow(new NotFoundException("Not Found")).when(accountService).deleteAccount(any(Long.class));

        try {
            Account result = resources.target("/accounts/1").request().get(Account.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus(), equalTo(404));
            assertThat(e.getResponse().getMediaType().toString(), equalTo(MediaType.APPLICATION_JSON));
        }
    }


}
