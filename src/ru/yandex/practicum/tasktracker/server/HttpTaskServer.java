package ru.yandex.practicum.tasktracker.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.tasktracker.manager.Managers;
import ru.yandex.practicum.tasktracker.manager.TaskManager;
import ru.yandex.practicum.tasktracker.manager.exception.TaskIntersectionException;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static final int RESPONSE_CODE_OK = 200;
    public static final int RESPONSE_CODE_CREATED = 201;
    public static final int RESPONSE_CODE_BAD_REQUEST = 400;
    public static final int RESPONSE_CODE_NOT_FOUND = 404;
    public static final int RESPONSE_CODE_METHOD_NOT_ALLOWED = 405;

    public static final String ENDPOINT_NOT_ALLOWED = "Endpoint not allowed";
    public static final String TASK_NOT_FOUND = "Task with the specified ID was not found";
    public static final String EPIC_NOT_FOUND = "Epic with the specified ID was not found";
    public static final String SUBTASK_NOT_FOUND = "Subtask with the specified ID was not found";
    public static final String TASK_DELETED_SUCCESSFULLY = "Task deleted successfully";
    public static final String EPIC_DELETED_SUCCESSFULLY = "Epic deleted successfully";
    public static final String SUBTASK_DELETED_SUCCESSFULLY = "Subtask deleted successfully";
    public static final String TASKS_DELETED_SUCCESSFULLY = "All tasks deleted successfully";
    public static final String EPICS_DELETED_SUCCESSFULLY = "All epics deleted successfully";
    public static final String SUBTASKS_DELETED_SUCCESSFULLY = "All subtasks deleted successfully";
    public static final String INCORRECT_JSON_RECEIVED = "Incorrect JSON received";
    public static final String TASK_CREATED_SUCCESSFULLY = "Task created successfully";
    public static final String TASK_UPDATED_SUCCESSFULLY = "Task updated successfully";
    public static final String EPIC_CREATED_SUCCESSFULLY = "Epic created successfully";
    public static final String EPIC_UPDATED_SUCCESSFULLY = "Epic updated successfully";
    public static final String SUBTASK_CREATED_SUCCESSFULLY = "Subtask created successfully";
    public static final String SUBTASK_UPDATED_SUCCESSFULLY = "Subtask updated successfully";

    private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

    private final TaskManager taskManager;
    private final Gson defaultGson;
    private final Gson taskGson;
    private final Gson epicGson;
    private final Gson subTaskGson;
    private final HttpServer server;

    private final Map<String, List<Endpoint>> paths = new HashMap<>();

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;

        defaultGson = Managers.getDefaultGson();
        taskGson = Managers.getTaskGson(taskManager);
        epicGson = Managers.getEpicGson(taskManager);
        subTaskGson = Managers.getSubTaskGson(taskManager);

        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handleTasks);
        fillPaths();
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private void handleTasks(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getRawQuery();
            if (query != null) {
                path += "?" + query;
            }

            Endpoint endpoint = getEndpoint(path, exchange.getRequestMethod());

            switch (endpoint) {
                case GET_HISTORY:
                    handleGetHistory(exchange);
                    break;
                case GET_TASKS:
                    handleGetTasks(exchange);
                    break;
                case GET_EPICS:
                    handleGetEpics(exchange);
                    break;
                case GET_SUBTASKS:
                    handleSubTasks(exchange);
                    break;
                case GET_SUBTASKS_BY_EPIC:
                    handleGetSubTasksByEpic(exchange, query);
                    break;
                case GET_TASK_BY_ID:
                    handleGetTaskById(exchange, query);
                    break;
                case GET_EPIC_BY_ID:
                    handleGetEpicById(exchange, query);
                    break;
                case GET_SUBTASK_BY_ID:
                    handleGetSubTaskById(exchange, query);
                    break;
                case DELETE_TASK_BY_ID:
                    handleDeleteTaskById(exchange, query);
                    break;
                case DELETE_EPIC_BY_ID:
                    handleDeleteEpicById(exchange, query);
                    break;
                case DELETE_SUBTASK_BY_ID:
                    handleDeleteSubTaskById(exchange, query);
                    break;
                case DELETE_TASKS:
                    handleDeleteTasks(exchange);
                    break;
                case DELETE_EPICS:
                    handleDeleteEpics(exchange);
                    break;
                case DELETE_SUBTASKS:
                    handleDeleteSubTasks(exchange);
                    break;
                case POST_TASK:
                    handlePostTask(exchange);
                    break;
                case POST_EPIC:
                    handlePostEpic(exchange);
                    break;
                case POST_SUBTASK:
                    handlePostSubTask(exchange);
                    break;
                case GET_PRIORITIZED_TASKS:
                    handleGetPrioritizedTasks(exchange);
                    break;
                default:
                    writeResponse(exchange, RESPONSE_CODE_METHOD_NOT_ALLOWED, ENDPOINT_NOT_ALLOWED,
                            CONTENT_TYPE_TEXT_PLAIN);
            }
        } finally {
            exchange.close();
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        return paths.keySet().stream()
                .filter(requestPath::matches)
                .map(paths::get)
                .flatMap(List::stream)
                .filter(endpoint -> endpoint.getRequestMethod().equals(requestMethod))
                .findFirst()
                .orElse(Endpoint.UNKNOWN);
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        writeResponse(exchange, RESPONSE_CODE_OK, taskGson.toJson(taskManager.getHistory()),
                CONTENT_TYPE_APPLICATION_JSON);
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, RESPONSE_CODE_OK, defaultGson.toJson(taskManager.getTasks()),
                CONTENT_TYPE_APPLICATION_JSON);
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        writeResponse(exchange, RESPONSE_CODE_OK, epicGson.toJson(taskManager.getEpics()),
                CONTENT_TYPE_APPLICATION_JSON);
    }

    private void handleSubTasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, RESPONSE_CODE_OK, subTaskGson.toJson(taskManager.getSubTasks()),
                CONTENT_TYPE_APPLICATION_JSON);
    }

    private void handleGetSubTasksByEpic(HttpExchange exchange, String query) throws IOException {
        int epicId = getTaskId(query);

        if (isValidEpic(epicId)) {
            writeResponse(exchange, RESPONSE_CODE_OK, subTaskGson.toJson(taskManager.getSubTasksByEpic(epicId)),
                    CONTENT_TYPE_APPLICATION_JSON);
        } else {
            writeResponse(exchange, RESPONSE_CODE_NOT_FOUND, EPIC_NOT_FOUND,
                    CONTENT_TYPE_TEXT_PLAIN);
        }
    }

    private void handleGetTaskById(HttpExchange exchange, String query) throws IOException {
        int taskId = getTaskId(query);

        if (isValidTask(taskId)) {
            writeResponse(exchange, RESPONSE_CODE_OK, defaultGson.toJson(taskManager.getTaskById(taskId)),
                    CONTENT_TYPE_APPLICATION_JSON);
        } else {
            writeResponse(exchange, RESPONSE_CODE_NOT_FOUND, TASK_NOT_FOUND,
                    CONTENT_TYPE_TEXT_PLAIN);
        }
    }

    private void handleGetEpicById(HttpExchange exchange, String query) throws IOException {
        int epicId = getTaskId(query);

        if (isValidEpic(epicId)) {
            writeResponse(exchange, RESPONSE_CODE_OK, epicGson.toJson(taskManager.getEpicById(epicId)),
                    CONTENT_TYPE_APPLICATION_JSON);
        } else {
            writeResponse(exchange, RESPONSE_CODE_NOT_FOUND, EPIC_NOT_FOUND,
                    CONTENT_TYPE_TEXT_PLAIN);
        }
    }

    private void handleGetSubTaskById(HttpExchange exchange, String query) throws IOException {
        int subTaskId = getTaskId(query);

        if (isValidSubTask(subTaskId)) {
            writeResponse(exchange, RESPONSE_CODE_OK, subTaskGson.toJson(taskManager.getSubTaskById(subTaskId)),
                    CONTENT_TYPE_APPLICATION_JSON);
        } else {
            writeResponse(exchange, RESPONSE_CODE_NOT_FOUND, SUBTASK_NOT_FOUND,
                    CONTENT_TYPE_TEXT_PLAIN);
        }
    }

    private void handleDeleteTaskById(HttpExchange exchange, String query) throws IOException {
        int taskId = getTaskId(query);

        if (isValidTask(taskId)) {
            taskManager.deleteTaskById(taskId);
            writeResponse(exchange, RESPONSE_CODE_OK, TASK_DELETED_SUCCESSFULLY,
                    CONTENT_TYPE_TEXT_PLAIN);
        } else {
            writeResponse(exchange, RESPONSE_CODE_NOT_FOUND, TASK_NOT_FOUND,
                    CONTENT_TYPE_TEXT_PLAIN);
        }
    }

    private void handleDeleteEpicById(HttpExchange exchange, String query) throws IOException {
        int epicId = getTaskId(query);

        if (isValidEpic(epicId)) {
            taskManager.deleteEpicById(epicId);
            writeResponse(exchange, RESPONSE_CODE_OK, EPIC_DELETED_SUCCESSFULLY,
                    CONTENT_TYPE_TEXT_PLAIN);
        } else {
            writeResponse(exchange, RESPONSE_CODE_NOT_FOUND, EPIC_NOT_FOUND,
                    CONTENT_TYPE_TEXT_PLAIN);
        }
    }

    private void handleDeleteSubTaskById(HttpExchange exchange, String query) throws IOException {
        int subTaskId = getTaskId(query);

        if (isValidSubTask(subTaskId)) {
            taskManager.deleteSubTaskById(subTaskId);
            writeResponse(exchange, RESPONSE_CODE_OK, SUBTASK_DELETED_SUCCESSFULLY,
                    CONTENT_TYPE_TEXT_PLAIN);
        } else {
            writeResponse(exchange, RESPONSE_CODE_NOT_FOUND, SUBTASK_NOT_FOUND,
                    CONTENT_TYPE_TEXT_PLAIN);
        }
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteTasks();
        writeResponse(exchange, RESPONSE_CODE_OK, TASKS_DELETED_SUCCESSFULLY,
                CONTENT_TYPE_TEXT_PLAIN);
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        taskManager.deleteEpics();
        writeResponse(exchange, RESPONSE_CODE_OK, EPICS_DELETED_SUCCESSFULLY,
                CONTENT_TYPE_TEXT_PLAIN);
    }

    private void handleDeleteSubTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteSubTasks();
        writeResponse(exchange, RESPONSE_CODE_OK, SUBTASKS_DELETED_SUCCESSFULLY,
                CONTENT_TYPE_TEXT_PLAIN);
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        String taskToJson = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

        try {
            Task task = defaultGson.fromJson(taskToJson, Task.class);

            try {
                if (task.getId() == 0) {
                    taskManager.createTask(task);
                    writeResponse(exchange, RESPONSE_CODE_CREATED, TASK_CREATED_SUCCESSFULLY,
                            CONTENT_TYPE_TEXT_PLAIN);
                } else if (isValidTask(task.getId())) {
                    taskManager.updateTask(task);
                    writeResponse(exchange, RESPONSE_CODE_OK, TASK_UPDATED_SUCCESSFULLY,
                            CONTENT_TYPE_TEXT_PLAIN);
                } else {
                    writeResponse(exchange, RESPONSE_CODE_BAD_REQUEST, TASK_NOT_FOUND,
                            CONTENT_TYPE_TEXT_PLAIN);
                }
            } catch (TaskIntersectionException e) {
                writeResponse(exchange, RESPONSE_CODE_BAD_REQUEST, e.getMessage(),
                        CONTENT_TYPE_TEXT_PLAIN);
            }
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, RESPONSE_CODE_BAD_REQUEST, INCORRECT_JSON_RECEIVED,
                    CONTENT_TYPE_TEXT_PLAIN);
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        String epicToJson = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

        try {
            Epic epic = epicGson.fromJson(epicToJson, Epic.class);

            if (epic.getId() == 0) {
                taskManager.createEpic(epic);
                writeResponse(exchange, RESPONSE_CODE_CREATED, EPIC_CREATED_SUCCESSFULLY,
                        CONTENT_TYPE_TEXT_PLAIN);
            } else if (isValidEpic(epic.getId())) {
                taskManager.updateEpic(epic);
                writeResponse(exchange, RESPONSE_CODE_OK, EPIC_UPDATED_SUCCESSFULLY,
                        CONTENT_TYPE_TEXT_PLAIN);
            } else {
                writeResponse(exchange, RESPONSE_CODE_BAD_REQUEST, EPIC_NOT_FOUND,
                        CONTENT_TYPE_TEXT_PLAIN);
            }
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, RESPONSE_CODE_BAD_REQUEST, INCORRECT_JSON_RECEIVED,
                    CONTENT_TYPE_TEXT_PLAIN);
        }
    }

    private void handlePostSubTask(HttpExchange exchange) throws IOException {
        String subTaskToJson = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

        try {
            SubTask subTask = subTaskGson.fromJson(subTaskToJson, SubTask.class);

            try {
                if (subTask.getId() == 0) {
                    taskManager.createSubTask(subTask);
                    writeResponse(exchange, RESPONSE_CODE_CREATED, SUBTASK_CREATED_SUCCESSFULLY,
                            CONTENT_TYPE_TEXT_PLAIN);
                } else if (isValidSubTask(subTask.getId())) {
                    taskManager.updateSubTask(subTask);
                    writeResponse(exchange, RESPONSE_CODE_OK, SUBTASK_UPDATED_SUCCESSFULLY,
                            CONTENT_TYPE_TEXT_PLAIN);
                } else {
                    writeResponse(exchange, RESPONSE_CODE_BAD_REQUEST, SUBTASK_NOT_FOUND,
                            CONTENT_TYPE_TEXT_PLAIN);
                }
            } catch (TaskIntersectionException e) {
                writeResponse(exchange, RESPONSE_CODE_BAD_REQUEST, e.getMessage(),
                        CONTENT_TYPE_TEXT_PLAIN);
            }
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, RESPONSE_CODE_BAD_REQUEST, INCORRECT_JSON_RECEIVED,
                    CONTENT_TYPE_TEXT_PLAIN);
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, RESPONSE_CODE_OK, taskGson.toJson(taskManager.getPrioritizedTasks()),
                CONTENT_TYPE_APPLICATION_JSON);
    }

    private void writeResponse(HttpExchange exchange, int responseCode, String responseString,
                               String contentType) throws IOException {
        byte[] bytes = new byte[0];
        int responseLength = 0;

        if (!responseString.isBlank()) {
            bytes = responseString.getBytes(DEFAULT_CHARSET);
            responseLength = bytes.length;
        }

        if (!contentType.isBlank()) {
            exchange.getResponseHeaders().set("Content-Type", contentType);
        }

        exchange.sendResponseHeaders(responseCode, responseLength);

        if (responseLength > 0) {
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(bytes);
            }
        }
    }

    private int getTaskId(String query) {
        return Integer.parseInt(query.replaceFirst("id=", ""));
    }

    private boolean isValidTask(int taskId) {
        return taskManager.getTasks().stream()
                .map(Task::getId)
                .anyMatch(id -> taskId == id);
    }

    private boolean isValidEpic(int epicId) {
        return taskManager.getEpics().stream()
                .map(Epic::getId)
                .anyMatch(id -> epicId == id);
    }

    private boolean isValidSubTask(int subTaskId) {
        return taskManager.getSubTasks().stream()
                .map(SubTask::getId)
                .anyMatch(id -> subTaskId == id);
    }

    private void fillPaths() {
        paths.put("/tasks/", List.of(Endpoint.GET_PRIORITIZED_TASKS));
        paths.put("/tasks/history/", List.of(Endpoint.GET_HISTORY));
        paths.put("/tasks/task/", List.of(Endpoint.GET_TASKS, Endpoint.POST_TASK, Endpoint.DELETE_TASKS));
        paths.put("/tasks/epic/", List.of(Endpoint.GET_EPICS, Endpoint.POST_EPIC, Endpoint.DELETE_EPICS));
        paths.put("/tasks/subtask/", List.of(Endpoint.GET_SUBTASKS, Endpoint.POST_SUBTASK, Endpoint.DELETE_SUBTASKS));
        paths.put("/tasks/subtask/epic/\\?id=\\d+", List.of(Endpoint.GET_SUBTASKS_BY_EPIC));
        paths.put("/tasks/task/\\?id=\\d+", List.of(Endpoint.GET_TASK_BY_ID, Endpoint.DELETE_TASK_BY_ID));
        paths.put("/tasks/epic/\\?id=\\d+", List.of(Endpoint.GET_EPIC_BY_ID, Endpoint.DELETE_EPIC_BY_ID));
        paths.put("/tasks/subtask/\\?id=\\d+", List.of(Endpoint.GET_SUBTASK_BY_ID, Endpoint.DELETE_SUBTASK_BY_ID));
    }
}