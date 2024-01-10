package ru.practicum.exeption;

public class TimeViolationException extends RuntimeException {
    public TimeViolationException(String message) {
        super(message);
    }
}
