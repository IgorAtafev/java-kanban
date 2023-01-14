package ru.yandex.practicum.tasktracker.manager.exception;

public class HttpRequestSendException extends RuntimeException {
    public HttpRequestSendException() {
    }

    public HttpRequestSendException(String message, Throwable cause) {
        super(message, cause);
    }
}