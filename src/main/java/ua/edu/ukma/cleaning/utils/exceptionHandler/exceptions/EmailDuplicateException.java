package ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions;

public class EmailDuplicateException extends RuntimeException {
    public EmailDuplicateException(String message) {
        super(message);
    }
}
