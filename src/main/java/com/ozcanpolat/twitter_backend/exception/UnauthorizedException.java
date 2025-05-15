package com.ozcanpolat.twitter_backend.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends com.ozcanpolat.twitter_backend.exception.TwitterException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}