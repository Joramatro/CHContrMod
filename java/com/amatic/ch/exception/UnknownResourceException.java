package com.amatic.ch.exception;

/**
 * Simulated business-logic exception indicating a desired business entity or
 * record cannot be found.
 */
public class UnknownResourceException extends RuntimeException {

    public UnknownResourceException(String msg) {
	super(msg);
    }

    public UnknownResourceException() {

    }
}
