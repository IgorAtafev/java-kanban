package ru.yandex.practicum.tasktracker.manager.exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException() {
    }

    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}