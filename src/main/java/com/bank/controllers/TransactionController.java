package com.bank.controllers;

import com.bank.domain.Transaction;
import com.bank.services.TransactionService;
import com.bank.services.exceptions.TransactionExecutionException;
import io.dropwizard.hibernate.UnitOfWork;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionController {

    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GET
    @UnitOfWork
    public List<Transaction> getTransactions() {
        return transactionService.getTransactions();
    }

    @POST
    @UnitOfWork
    public Transaction executeTransaction(@Valid Transaction transaction) throws TransactionExecutionException {
        return transactionService.executeTransaction(transaction);
    }

    @GET
    @Path("/{transactionId}")
    @UnitOfWork
    public Transaction getTransaction(@PathParam("transactionId") @NotNull long transactionId) {
        return transactionService.getTransaction(transactionId);
    }

}
