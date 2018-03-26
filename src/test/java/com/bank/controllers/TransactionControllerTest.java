package com.bank.controllers;

import com.bank.config.NotFoundExceptionMapper;
import com.bank.domain.Money;
import com.bank.domain.Transaction;
import com.bank.domain.TransactionResult;
import com.bank.domain.TransactionStatus;
import com.bank.services.TransactionService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Currency;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TransactionControllerTest {

    private static final TransactionService transactionServiceMock = mock(TransactionService.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TransactionController(transactionServiceMock))
            .setRegisterDefaultExceptionMappers(false)
            .addProvider(NotFoundExceptionMapper.class).build();

    @After
    public void tearDown(){
        reset(transactionServiceMock);
    }

    @Test
    public void testGetValidTransaction() {
        Transaction transaction = new Transaction(123, 234, new Money(Currency.getInstance("GBP"), 5));
        when(transactionServiceMock.getTransaction(any(Long.class))).thenReturn(transaction);

        Transaction result = resources.target("/transactions/1").request().get(Transaction.class);

        assertThat(result.getRemitterAccountId(), equalTo(123L));
        assertThat(result.getAmount().getValue(), equalTo(5.0));
        verify(transactionServiceMock).getTransaction(1);
    }

    @Test
    public void testGetInvalidTransaction() {
        when(transactionServiceMock.getTransaction(any(Long.class))).thenThrow(new NotFoundException("Not Found"));
        
        WebApplicationException expectedException = null;
        try {
            resources.target("/transactions/1").request().get(Transaction.class);
        } catch (WebApplicationException e) {
            expectedException = e;
        }
        assertThat(expectedException, notNullValue());
        assertThat(expectedException.getResponse().getStatus(), equalTo(404));
        assertThat(expectedException.getResponse().getMediaType().toString(), equalTo(MediaType.APPLICATION_JSON));
        verify(transactionServiceMock).getTransaction(any(Long.class));
    }

    @Test
    public void testGetTransactions() {
        Response result = resources.target("/transactions").request().get();

        assertThat(result.getStatusInfo(), equalTo(Response.Status.OK));
        verify(transactionServiceMock).getTransactions();
    }

    @Test
    public void testExecuteValidTransaction() {
        Transaction transaction = new Transaction(123, 234, new Money(Currency.getInstance("GBP"), 5));
        transaction.setTransactionResult(new TransactionResult(TransactionStatus.EXECUTED, "SUCCESS"));
        when(transactionServiceMock.executeTransaction(any(Transaction.class))).thenReturn(transaction);

        Response result = resources.target("/transactions").request().post(Entity.entity(transaction, MediaType.APPLICATION_JSON));

        assertThat(result.getStatusInfo(), equalTo(Response.Status.CREATED));
        verify(transactionServiceMock).executeTransaction(any(Transaction.class));
    }

    @Test
    public void testFailsToExecuteTransactionWhenBalanceNull() {
        Transaction transaction = new Transaction(123, 234, null);

        Response result = resources.target("/transactions").request().post(Entity.entity(transaction, MediaType.APPLICATION_JSON));

        assertThat(result.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
        verify(transactionServiceMock, times(0)).executeTransaction(any(Transaction.class));
    }

    @Test
    public void testFailsToExecuteTransactionWhenBalanceCurrencyNull() {
        Transaction transaction = new Transaction(123, 234, new Money(null, 5));

        Response result = resources.target("/transactions").request().post(Entity.entity(transaction, MediaType.APPLICATION_JSON));

        assertThat(result.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
        verify(transactionServiceMock, times(0)).executeTransaction(any(Transaction.class));
    }

    @Test
    public void testFailsToExecuteTransactionWhenBalanceNegative() {
        Transaction transaction = new Transaction(123, 234, new Money(Currency.getInstance("GBP"), -5));

        Response result = resources.target("/transactions").request().post(Entity.entity(transaction, MediaType.APPLICATION_JSON));

        assertThat(result.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
        verify(transactionServiceMock, times(0)).executeTransaction(any(Transaction.class));
    }

    @Test
    public void testExecuteTransactionInsufficientFunds() {
        Transaction transaction = new Transaction(123, 234, new Money(Currency.getInstance("GBP"), 5));
        transaction.setTransactionResult(new TransactionResult(TransactionStatus.FAILED, "Insufficient funds"));
        when(transactionServiceMock.executeTransaction(any(Transaction.class))).thenReturn(transaction);

        Response result = resources.target("/transactions").request().post(Entity.entity(transaction, MediaType.APPLICATION_JSON));

        assertThat(result.getStatus(), equalTo(422));
        verify(transactionServiceMock, times(1)).executeTransaction(any(Transaction.class));
    }

    @Test
    public void testExecuteTransactionInvalidAccount() {
        Transaction transaction = new Transaction(123, 234, new Money(Currency.getInstance("GBP"), 5));
        transaction.setTransactionResult(new TransactionResult(TransactionStatus.FAILED, "Invalid account"));
        when(transactionServiceMock.executeTransaction(any(Transaction.class))).thenReturn(transaction);

        Response result = resources.target("/transactions").request().post(Entity.entity(transaction, MediaType.APPLICATION_JSON));

        assertThat(result.getStatus(), equalTo(422));
        verify(transactionServiceMock, times(1)).executeTransaction(any(Transaction.class));
    }

}
