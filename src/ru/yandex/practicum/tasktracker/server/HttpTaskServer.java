package ru.yandex.practicum.tasktracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.tasktracker.manager.FileBackedTaskManager;
import ru.yandex.practicum.tasktracker.manager.TaskManager;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.util.EpicAdapter;
import ru.yandex.practicum.tasktracker.util.SubTaskAdapter;
import ru.yandex.practicum.tasktracker.util.TaskAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final HttpServer httpServer;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;

        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(FileBackedTaskManager.loadFromFile("tasks.csv"));
        httpTaskServer.start();
        httpTaskServer.stop();
    }

    class TaskHandler implements HttpHandler {
        private static final int RESPONSE_CODE_OK = 200;
        private static final int RESPONSE_CODE_NOT_FOUND = 404;
        private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
        private static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

        private final Gson defaultGson = new Gson();
        private final Gson epicGson = new GsonBuilder()
                .registerTypeAdapter(SubTask.class, new SubTaskAdapter())
                .create();
        private final Gson subTaskGson = new GsonBuilder()
                .registerTypeAdapter(Epic.class, new EpicAdapter())
                .create();
        private final Gson historyGson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskAdapter())
                .registerTypeAdapter(Epic.class, new EpicAdapter())
                .registerTypeAdapter(SubTask.class, new SubTaskAdapter())
                .create();

        private final Map<String, List<Endpoint>> paths = new HashMap<>();

        {
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

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            if (query != null) {
                path += "?" + query;
            }

            Endpoint endpoint = getEndpoint(path, exchange.getRequestMethod());

            switch (endpoint) {
                case GET_TASKS:
                    writeResponse(exchange, RESPONSE_CODE_OK, defaultGson.toJson(taskManager.getTasks()),
                            CONTENT_TYPE_APPLICATION_JSON);
                    break;
                case GET_EPICS:
                    writeResponse(exchange, RESPONSE_CODE_OK, epicGson.toJson(taskManager.getEpics()),
                            CONTENT_TYPE_APPLICATION_JSON);
                    break;
                case GET_SUBTASKS:
                    writeResponse(exchange, RESPONSE_CODE_OK, subTaskGson.toJson(taskManager.getSubTasks()),
                            CONTENT_TYPE_APPLICATION_JSON);
                    break;
                case GET_HISTORY:
                    writeResponse(exchange, RESPONSE_CODE_OK, historyGson.toJson(taskManager.getHistory()),
                            CONTENT_TYPE_APPLICATION_JSON);
                    break;
                default:
                    writeResponse(exchange, RESPONSE_CODE_NOT_FOUND, "Endpoint does not exist",
                            CONTENT_TYPE_TEXT_PLAIN);
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
    }

    private enum Endpoint {
        GET_PRIORITIZED_TASKS("GET"),
        GET_HISTORY("GET"),
        GET_TASKS("GET"),
        GET_EPICS("GET"),
        GET_SUBTASKS("GET"),
        GET_SUBTASKS_BY_EPIC("GET"),
        GET_TASK_BY_ID("GET"),
        GET_EPIC_BY_ID("GET"),
        GET_SUBTASK_BY_ID("GET"),
        DELETE_TASKS("DELETE"),
        DELETE_EPICS("DELETE"),
        DELETE_SUBTASKS("DELETE"),
        DELETE_TASK_BY_ID("DELETE"),
        DELETE_EPIC_BY_ID("DELETE"),
        DELETE_SUBTASK_BY_ID("DELETE"),
        POST_TASK("POST"),
        POST_EPIC("POST"),
        POST_SUBTASK("POST"),
        UNKNOWN("");

        private String requestMethod;

        Endpoint(String requestMethod) {
            this.requestMethod = requestMethod;
        }

        String getRequestMethod() {
            return requestMethod;
        }
    }
}