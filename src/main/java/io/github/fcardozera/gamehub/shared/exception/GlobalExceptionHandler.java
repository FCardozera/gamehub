package io.github.fcardozera.gamehub.shared.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.fcardozera.gamehub.identity.exception.EmailAlreadyUsedException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import java.net.URI;
import java.util.Map;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice()
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));

        logger.error("Unexpected error handling request", ex);

        return problemDetail;
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ProblemDetail handleEmailAlreadyUsedException(EmailAlreadyUsedException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Email already used");
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "One or more fields are invalid");
        Map<String, String> errors = new HashMap<>();
        for (var error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        problemDetail.setProperty("errors", errors);
        problemDetail.setTitle("Invalid method argument");
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));

        return problemDetail;
    }

    // TODO: private method buildProblemDetail to avoid code duplication (DRY) in
    // the exception handlers above
}
