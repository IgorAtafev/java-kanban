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

    private final TaskManager taskManager;
    private final Gson defaultGson;
    private final Gson taskGson;
    private final Gson epicGson;
    private final Gson subTaskGson;
    private  HttpServer server;

    private final Map<String, List<Endpoint>> paths = new HashMap<>();

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;

        defaultGson = Managers.getDefaultGson();
        taskGson = Managers.getTaskGson(taskManager);
        epicGson = Managers.getEpicGson(taskManager);
        subTaskGson = Managers.getSubTaskGson(taskManager);

        fillPaths();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handleTasks);
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
                    writeResponse(exchange, 405,"Endpoint not allowed","text/plain");
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
        writeResponse(exchange, 200, taskGson.toJson(taskManager.getHistory()),
                "application/json");
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, 200, defaultGson.toJson(taskManager.getTasks()),
                "application/json");
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        writeResponse(exchange, 200, epicGson.toJson(taskManager.getEpics()),
                "application/json");
    }

    private void handleSubTasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, 200, subTaskGson.toJson(taskManager.getSubTasks()),
                "application/json");
    }

    private void handleGetSubTasksByEpic(HttpExchange exchange, String query) throws IOException {
        int epicId = getTaskId(query);

        if (isValidEpic(epicId)) {
            writeResponse(exchange, 200, subTaskGson.toJson(taskManager.getSubTasksByEpic(epicId)),
                    "application/json");
        } else {
            writeResponse(exchange, 404, "Epic with the specified ID was not found",
                    "text/plain");
        }
    }

    private void handleGetTaskById(HttpExchange exchange, String query) throws IOException {
        int taskId = getTaskId(query);

        if (isValidTask(taskId)) {
            writeResponse(exchange, 200, defaultGson.toJson(taskManager.getTaskById(taskId)),
                    "application/json");
        } else {
            writeResponse(exchange, 404, "Task with the specified ID was not found",
                    "text/plain");
        }
    }

    private void handleGetEpicById(HttpExchange exchange, String query) throws IOException {
        int epicId = getTaskId(query);

        if (isValidEpic(epicId)) {
            writeResponse(exchange, 200, epicGson.toJson(taskManager.getEpicById(epicId)),
                    "application/json");
        } else {
            writeResponse(exchange, 404, "Epic with the specified ID was not found",
                    "text/plain");
        }
    }

    private void handleGetSubTaskById(HttpExchange exchange, String query) throws IOException {
        int subTaskId = getTaskId(query);

        if (isValidSubTask(subTaskId)) {
            writeResponse(exchange, 200, subTaskGson.toJson(taskManager.getSubTaskById(subTaskId)),
                    "application/json");
        } else {
            writeResponse(exchange, 404, "Subtask with the specified ID was not found",
                    "text/plain");
        }
    }

    private void handleDeleteTaskById(HttpExchange exchange, String query) throws IOException {
        int taskId = getTaskId(query);

        if (isValidTask(taskId)) {
            taskManager.deleteTaskById(taskId);
            writeResponse(exchange, 200, "Task deleted successfully","text/plain");
        } else {
            writeResponse(exchange, 404, "Task with the specified ID was not found",
                    "text/plain");
        }
    }

    private void handleDeleteEpicById(HttpExchange exchange, String query) throws IOException {
        int epicId = getTaskId(query);

        if (isValidEpic(epicId)) {
            taskManager.deleteEpicById(epicId);
            writeResponse(exchange, 200, "Epic deleted successfully","text/plain");
        } else {
            writeResponse(exchange, 404, "Epic with the specified ID was not found",
                    "text/plain");
        }
    }

    private void handleDeleteSubTaskById(HttpExchange exchange, String query) throws IOException {
        int subTaskId = getTaskId(query);

        if (isValidSubTask(subTaskId)) {
            taskManager.deleteSubTaskById(subTaskId);
            writeResponse(exchange, 200, "Subtask deleted successfully","text/plain");
        } else {
            writeResponse(exchange, 404, "Subtask with the specified ID was not found",
                    "text/plain");
        }
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteTasks();
        writeResponse(exchange, 200, "All tasks deleted successfully","text/plain");
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        taskManager.deleteEpics();
        writeResponse(exchange, 200, "All epics deleted successfully","text/plain");
    }

    private void handleDeleteSubTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteSubTasks();
        writeResponse(exchange, 200, "All subtasks deleted successfully","text/plain");
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        String taskToJson = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

        try {
            Task task = defaultGson.fromJson(taskToJson, Task.class);

            try {
                if (task.getId() == 0) {
                    taskManager.createTask(task);
                    writeResponse(exchange, 201, "Task created successfully",
                            "text/plain");
                } else if (isValidTask(task.getId())) {
                    taskManager.updateTask(task);
                    writeResponse(exchange, 200, "Task updated successfully",
                            "text/plain");
                } else {
                    writeResponse(exchange, 400, "Task with the specified ID was not found",
                            "text/plain");
                }
            } catch (TaskIntersectionException e) {
                writeResponse(exchange, 400, e.getMessage(),"text/plain");
            }
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, 400, "Incorrect JSON received","text/plain");
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        String epicToJson = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

        try {
            Epic epic = epicGson.fromJson(epicToJson, Epic.class);

            if (epic.getId() == 0) {
                taskManager.createEpic(epic);
                writeResponse(exchange, 201, "Epic created successfully","text/plain");
            } else if (isValidEpic(epic.getId())) {
                taskManager.updateEpic(epic);
                writeResponse(exchange, 200, "Epic updated successfully","text/plain");
            } else {
                writeResponse(exchange, 400, "Epic with the specified ID was not found",
                        "text/plain");
            }
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, 400, "Incorrect JSON received","text/plain");
        }
    }

    private void handlePostSubTask(HttpExchange exchange) throws IOException {
        String subTaskToJson = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

        try {
            SubTask subTask = subTaskGson.fromJson(subTaskToJson, SubTask.class);

            try {
                if (subTask.getId() == 0) {
                    taskManager.createSubTask(subTask);
                    writeResponse(exchange, 201, "Subtask created successfully",
                            "text/plain");
                } else if (isValidSubTask(subTask.getId())) {
                    taskManager.updateSubTask(subTask);
                    writeResponse(exchange, 200, "Subtask updated successfully",
                            "text/plain");
                } else {
                    writeResponse(exchange, 400, "Subtask with the specified ID was not found",
                            "text/plain");
                }
            } catch (TaskIntersectionException e) {
                writeResponse(exchange, 400, e.getMessage(),"text/plain");
            }
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, 400, "Incorrect JSON received","text/plain");
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, 200, taskGson.toJson(taskManager.getPrioritizedTasks()),
                "application/json");
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
