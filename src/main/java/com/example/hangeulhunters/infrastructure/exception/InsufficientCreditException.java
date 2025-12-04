package com.example.hangeulhunters.infrastructure.exception;

/**
 * 크레딧 포인트 부족 예외
 */
public class InsufficientCreditException extends RuntimeException {
    public InsufficientCreditException(String message) {
        super(message);
    }
}
