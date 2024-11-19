package ua.edu.ukma.cleaning.utils.exception.handler.exceptions;

public class InvalidResponseException extends RuntimeException {
    public InvalidResponseException() {
        super("Invalid response from server");
    }
}
