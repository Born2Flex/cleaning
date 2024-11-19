package ua.edu.ukma.cleaning.utils.exception.handler;


import org.springframework.security.authorization.AuthorizationDeniedException;
import ua.edu.ukma.cleaning.utils.exception.handler.exceptions.AccessDeniedException;
import ua.edu.ukma.cleaning.utils.exception.handler.exceptions.AlreadyAppliedException;
import ua.edu.ukma.cleaning.utils.exception.handler.exceptions.CantChangeEntityException;
import ua.edu.ukma.cleaning.utils.exception.handler.exceptions.NoSuchEntityException;
import ua.edu.ukma.cleaning.utils.exception.handler.exceptions.ProposalNameDuplicateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("\n"));
        log.info("Validation on controller failed: {}", message);
        return new ResponseEntity<>(formatMessage(message), getHttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ProposalNameDuplicateException.class, CantChangeEntityException.class, AlreadyAppliedException.class})
    public ResponseEntity<String> handleDuplicateException(Exception e) {
        return new ResponseEntity<>(formatMessage(e.getMessage()), getHttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(formatMessage(e.getMessage()), getHttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoSuchEntityException.class)
    public ResponseEntity<String> handleNoSuchEntityException(NoSuchEntityException e) {
        return new ResponseEntity<>(formatMessage(e.getMessage()), getHttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleUnexpectedException(Exception e) {
        log.error(e.getMessage());
        log.error(e.getClass().toString());
        return new ResponseEntity<>(formatMessage(e.getMessage()), getHttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static String formatMessage(String message) {
        return "{\"errorMessage\":\"" + message + "\"}";
    }

    private static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
