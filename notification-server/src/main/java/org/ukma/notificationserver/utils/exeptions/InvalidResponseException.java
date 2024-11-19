package org.ukma.notificationserver.utils.exeptions;

public class InvalidResponseException extends RuntimeException {
    public InvalidResponseException() {
        super("Invalid response from server");
    }
}
