package ru.yandex.practicum.tasktracker.manager.exception;

public class TaskIntersectionException extends RuntimeException {

    public TaskIntersectionException() {
    }

    public TaskIntersectionException(String message) {
        super(message);
    }
}
