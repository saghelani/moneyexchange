package com.bank.config;

import com.bank.services.exceptions.TransactionExecutionException;
import io.dropwizard.jersey.errors.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class TransactionExecutionExceptionMapper implements ExceptionMapper<TransactionExecutionException> {

    @Override
    public Response toResponse(TransactionExecutionException e) {
        return Response.status(400)
                .entity(new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
