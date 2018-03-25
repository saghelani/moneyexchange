package com.bank.services.exceptions;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

public class TransactionExecutionException extends ClientErrorException {

    public TransactionExecutionException(String message) {
        super(message, Response.Status.BAD_REQUEST);
    }
}
