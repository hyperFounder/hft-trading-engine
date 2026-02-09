package com.fintech.trading.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.trading.domain.IdempotencyRecord;
import com.fintech.trading.exception.IdempotencyKeyMissingException;
import com.fintech.trading.repository.IdempotencyRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

    private final IdempotencyRepository repository;
    private final ObjectMapper objectMapper;

    @Around("@annotation(idempotent)")
    public Object enforceIdempotency(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String key = request.getHeader(idempotent.headerName());

        // If no key is provided, proceed throw error
        if (key == null || key.isBlank()) {
            throw new IdempotencyKeyMissingException("Idempotency key is missing");
        }

        // Check H2 for existing record
        var existing = repository.findById(key);
        if (existing.isPresent()) {
            String cachedBody = existing.get().getResponseBody();
            return CompletableFuture.completedFuture(ResponseEntity.ok(cachedBody));
        }

        // Proceed with original execution
        Object result = joinPoint.proceed();

        // Handle Async result to store the result once finished
        if (result instanceof CompletableFuture<?> future) {
            return future.thenApply(response -> {
                if (response instanceof ResponseEntity<?> res && res.getStatusCode().is2xxSuccessful()) {
                    try {
                        String bodyToStore = (res.getBody() instanceof String s)
                                ? s
                                : objectMapper.writeValueAsString(res.getBody());

                        repository.save(IdempotencyRecord.builder()
                                .requestKey(key)
                                .responseBody(bodyToStore)
                                .createdAt(LocalDateTime.now())
                                .build());
                    } catch (Exception e) {
                        System.out.println(e.getCause());
                    }
                }
                return response;
            });
        }
        return result;
    }
}