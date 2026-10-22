package com.in6206.collabdesk.exception;

/** A request that is well-formed but breaks a business rule (illegal transition, revision limit...). */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
