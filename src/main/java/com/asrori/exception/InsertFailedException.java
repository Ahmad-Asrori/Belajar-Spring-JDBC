package com.asrori.exception;

import org.springframework.dao.DataAccessException;

public class InsertFailedException extends DataAccessException {

    public InsertFailedException(String msg) {
        super(msg);
    }

}
