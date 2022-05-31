package com.boongdev.atdd.membership.common;

import com.boongdev.atdd.membership.exception.MembershipErrorResult;
import com.boongdev.atdd.membership.exception.MembershipException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        List<String> errorList = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("Invalid DTO Parameter errors : {}", errorList);

        return this.makeErrorResponseEntity(errorList.toString());
    }

    private ResponseEntity<Object> makeErrorResponseEntity(String errorDescription) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), errorDescription));
    }

    @ExceptionHandler({MembershipException.class})
    public ResponseEntity<ErrorResponse> handleRestApiException(MembershipException exception) {
        log.warn("MembershipException occur: ", exception);
        return this.makeErrorResponseEntity(exception.getMembershipErrorResult());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.warn("Exception occur: ", exception);
        return this.makeErrorResponseEntity(MembershipErrorResult.UNKNOWN_EXCEPTION);
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(MembershipErrorResult membershipErrorResult) {
        return ResponseEntity.status(membershipErrorResult.getHttpStatus())
                .body(new ErrorResponse(membershipErrorResult.getHttpStatus().toString(), membershipErrorResult.getMessage()));
    }

    @Getter
    @RequiredArgsConstructor
    private class ErrorResponse {
        private final String code;
        private final String message;
    }
}
