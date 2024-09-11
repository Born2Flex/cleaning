package ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
