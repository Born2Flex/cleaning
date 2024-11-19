package ua.edu.ukma.cleaning.utils.exception.handler.exceptions;

public class ClientRequestException extends RuntimeException {
    public ClientRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
