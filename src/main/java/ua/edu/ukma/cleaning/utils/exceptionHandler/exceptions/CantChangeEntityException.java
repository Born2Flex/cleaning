package ua.edu.ukma.cleaning.utils.exceptionHandler.exceptions;

public class CantChangeEntityException extends RuntimeException {
    public CantChangeEntityException(String message) {
        super(message);
    }
}
