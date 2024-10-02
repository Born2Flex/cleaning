package org.ukma.userserver.exceptions;

public class PhoneNumberDuplicateException extends RuntimeException {
    public PhoneNumberDuplicateException(String message) {
        super(message);
    }
}
