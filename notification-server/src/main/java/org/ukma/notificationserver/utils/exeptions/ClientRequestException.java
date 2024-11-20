package org.ukma.notificationserver.utils.exeptions;

public class ClientRequestException extends RuntimeException {
    public ClientRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
