package com.ernestasi.lettercounterback.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class LetterCounterException extends RuntimeException {

    public LetterCounterException(String exception) {
        super(exception);
    }
}
