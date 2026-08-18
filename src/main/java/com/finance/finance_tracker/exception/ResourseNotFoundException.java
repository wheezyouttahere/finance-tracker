package com.finance.finance_tracker.exception;

public class ResourseNotFoundException extends RuntimeException {
    public ResourseNotFoundException(String message) {
        super(message);
    }
}