package com.fintech.trading.advice;

import com.fintech.trading.exception.InsufficientFundsException;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalTradeExceptionHandler {

    private final MessageSource messageSource;

    public GlobalTradeExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Object> handleLiquidity(InsufficientFundsException ex) {
        String errorCode = getMessage("error.code.liquidity", "ERR_LIQUIDITY");
        String errorMsg = getMessage("error.msg.liquidity", new Object[]{ex.getMessage()}, ex.getMessage());
        return buildResponse(HttpStatus.PAYMENT_REQUIRED, errorCode, errorMsg);
    }

    @ExceptionHandler(NoSuchMessageException.class)
    public ResponseEntity<Object> handleMissingMessage(NoSuchMessageException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_CONFIG", "Missing message key in properties file: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        ex.printStackTrace(); // Print stack trace to console for debugging
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_INTERNAL", "Unexpected error: " + ex.getMessage());
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error_code", code);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

    private String getMessage(String code, String defaultMsg) {
        try {
            return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return defaultMsg;
        }
    }

    private String getMessage(String code, Object[] args, String defaultMsg) {
        try {
            return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return defaultMsg;
        }
    }
}