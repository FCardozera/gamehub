package io.github.fcardozera.gamehub.shared.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.fcardozera.gamehub.identity.exception.EmailAlreadyUsedException;
import io.github.fcardozera.gamehub.identity.exception.NicknameAlreadyUsedException;

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
        logger.error("Unexpected error handling request", ex);

        return buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred while processing the request.", request);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ProblemDetail handleEmailAlreadyUsedException(EmailAlreadyUsedException ex, WebRequest request) {
        return buildProblemDetail(HttpStatus.CONFLICT, "Email already used", ex.getMessage(), request);
    }

    @ExceptionHandler(NicknameAlreadyUsedException.class)
    public ProblemDetail handleNicknameAlreadyUsedException(NicknameAlreadyUsedException ex, WebRequest request) {
        return buildProblemDetail(HttpStatus.CONFLICT, "Nickname already used", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        ProblemDetail problemDetail = buildProblemDetail(HttpStatus.BAD_REQUEST, "Invalid method argument",
                "One or more method arguments are invalid.", request);
        Map<String, String> errors = new HashMap<>();
        for (var error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String title, String detail, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return problemDetail;
    }
}
