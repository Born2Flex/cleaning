package ua.edu.ukma.cleaning.utils.exceptions;

public class EmailDuplicateException extends RuntimeException {
    public EmailDuplicateException(String message) {
        super(message);
    }
}
