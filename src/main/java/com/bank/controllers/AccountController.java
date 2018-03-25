package com.bank.controllers;

import com.bank.domain.Account;
import com.bank.services.AccountService;
import io.dropwizard.hibernate.UnitOfWork;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountController{

    private AccountService accountService;

    @Context
    private UriInfo uriInfo;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    @UnitOfWork
    public List<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @POST
    @UnitOfWork
    public Response createOrUpdateAccount(@Valid Account account) {
        accountService.createOrUpdateAccount(account);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Long.toString(account.getId()));
        return Response.created(builder.build()).entity(account).build();
    }

    @GET
    @Path("/{accountId}")
    @UnitOfWork
    public Account getAccount(@PathParam("accountId") @NotNull long accountId) throws NotFoundException {
        return accountService.getAccount(accountId);
    }

    @DELETE
    @Path("/{accountId}")
    @UnitOfWork
    public void deleteAccount(@PathParam("accountId") @NotNull long accountId) throws NotFoundException {
        accountService.deleteAccount(accountId);
    }

}
