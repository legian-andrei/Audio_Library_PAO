package org.example.Exceptions;

public class DuplicateUsernameException extends Exception {
    public DuplicateUsernameException(String message) {
        super(message);
    }
}
