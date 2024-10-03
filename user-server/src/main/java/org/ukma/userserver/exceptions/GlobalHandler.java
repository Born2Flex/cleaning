package org.ukma.userserver.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.view.RedirectView;

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

    @ExceptionHandler({EmailDuplicateException.class, PhoneNumberDuplicateException.class})
    public ResponseEntity<String> handleDuplicateException(Exception e) {
        return new ResponseEntity<>(formatMessage(e.getMessage()), getHttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public RedirectView handleAccessDeniedException(AccessDeniedException e) {
        return new RedirectView("/");
    }

    @ExceptionHandler(NoSuchEntityException.class)
    public ResponseEntity<String> handleNoSuchEntityException(NoSuchEntityException e) {
        return new ResponseEntity<>(formatMessage(e.getMessage()), getHttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public String handleUnexpectedException(Exception e) {
        return "unexpectedError";
    }

    private static String formatMessage(String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"errorMessage\":\"")
                .append(message)
                .append("\"}");
        return sb.toString();
    }

    private static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
