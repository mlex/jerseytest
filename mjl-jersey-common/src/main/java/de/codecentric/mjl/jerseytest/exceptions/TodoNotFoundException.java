package de.codecentric.mjl.jerseytest.exceptions;

public class TodoNotFoundException extends RuntimeException {
    public TodoNotFoundException() {
	super();
    }

    public TodoNotFoundException(String msg) {
	super(msg);
    }
}
