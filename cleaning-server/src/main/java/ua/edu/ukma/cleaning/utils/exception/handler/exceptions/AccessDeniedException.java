package ua.edu.ukma.cleaning.utils.exception.handler.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("Access denied");
    }
}
