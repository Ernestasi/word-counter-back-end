package com.ernestasi.lettercounterback.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestDataException extends RuntimeException
{
    public BadRequestDataException(String exception) {
        super(exception);
    }
}

