package ru.practicum.exeption;

public class StatsValidationException extends RuntimeException {

    public StatsValidationException(String message) {
        super(message);
    }
}
