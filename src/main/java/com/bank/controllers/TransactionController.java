package com.bank.controllers;

import com.bank.domain.Transaction;
import com.bank.domain.TransactionStatus;
import com.bank.services.TransactionService;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.errors.ErrorMessage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionController {

    private TransactionService transactionService;

    @Context
    private UriInfo uriInfo;

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
    public Response executeTransaction(@Valid Transaction transaction) {
        Transaction result = transactionService.executeTransaction(transaction);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Long.toString(result.getId()));
        if(transaction.getTransactionResult().getTransactionStatus() == TransactionStatus.EXECUTED) {
            return Response.created(builder.build()).entity(transaction).build();
        } else {
            return Response.status(422).entity(new ErrorMessage(422, "TRANSACTION FAILED" ,
                    transaction.getTransactionResult().getReason())).build();
        }
    }

    @GET
    @Path("/{transactionId}")
    @UnitOfWork
    public Transaction getTransaction(@PathParam("transactionId") @NotNull long transactionId) {
        return transactionService.getTransaction(transactionId);
    }

}
