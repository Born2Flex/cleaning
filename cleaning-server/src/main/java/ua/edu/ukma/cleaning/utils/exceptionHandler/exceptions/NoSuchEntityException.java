package ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions;

public class NoSuchEntityException extends RuntimeException {
    public NoSuchEntityException(String message) {
        super(message);
    }
}
