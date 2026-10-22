package com.in6206.collabdesk.exception;

/** The user is logged in but does not own the resource. */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
