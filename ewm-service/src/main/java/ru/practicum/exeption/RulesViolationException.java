package ru.practicum.exeption;

public class RulesViolationException extends RuntimeException {
    public RulesViolationException(String message) {
        super(message);
    }
}