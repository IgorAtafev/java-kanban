package ru.yandex.practicum.tasktracker.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.tasktracker.manager.InMemoryTaskManager;
import ru.yandex.practicum.tasktracker.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTaskServer {
    private static final int PORT = 8082;

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
        HttpTaskServer httpTaskServer = new HttpTaskServer(new InMemoryTaskManager());
        httpTaskServer.start();
        httpTaskServer.stop();
    }

    private static class TaskHandler implements HttpHandler {
        private static final Map<String, List<Endpoint>> paths = new HashMap<>();

        static {
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
            System.out.println(endpoint);
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
}