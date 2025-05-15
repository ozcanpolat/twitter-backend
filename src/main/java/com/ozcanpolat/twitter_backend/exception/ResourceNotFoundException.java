package com.ozcanpolat.twitter_backend.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends TwitterException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}