package ua.edu.ukma.cleaning.utils.exception.handler.exceptions;

public class FileProcessingException extends RuntimeException {
    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileProcessingException(String message) {
        super(message);
    }
}
