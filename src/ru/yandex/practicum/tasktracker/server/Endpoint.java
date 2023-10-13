package ru.yandex.practicum.tasktracker.server;

public enum Endpoint {

    GET_HISTORY("GET"),
    GET_TASKS("GET"),
    GET_EPICS("GET"),
    GET_SUBTASKS("GET"),
    GET_SUBTASKS_BY_EPIC("GET"),
    GET_TASK_BY_ID("GET"),
    GET_EPIC_BY_ID("GET"),
    GET_SUBTASK_BY_ID("GET"),
    DELETE_TASK_BY_ID("DELETE"),
    DELETE_EPIC_BY_ID("DELETE"),
    DELETE_SUBTASK_BY_ID("DELETE"),
    DELETE_TASKS("DELETE"),
    DELETE_EPICS("DELETE"),
    DELETE_SUBTASKS("DELETE"),
    POST_TASK("POST"),
    POST_EPIC("POST"),
    POST_SUBTASK("POST"),
    GET_PRIORITIZED_TASKS("GET"),
    UNKNOWN("");

    private String requestMethod;

    Endpoint(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    String getRequestMethod() {
        return requestMethod;
    }
}
