package ru.yandex.practicum.tasktracker.manager;

public class TaskCreateOrUpdateException extends RuntimeException {
    public TaskCreateOrUpdateException() {
    }

    public TaskCreateOrUpdateException(String message) {
        super(message);
    }
}