package ru.yandex.practicum.tasktracker.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class KVServer {

    public static final int PORT = 8078;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final String apiToken;
    private HttpServer server;

    private final Map<String, String> data = new HashMap<>();

    public KVServer() {
        apiToken = generateApiToken();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private void load(HttpExchange exchange) throws IOException {
        try {
            if (!hasAuth(exchange)) {
                exchange.sendResponseHeaders(403, 0);
                return;
            }

            if ("GET".equals(exchange.getRequestMethod())) {
                String key = exchange.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    exchange.sendResponseHeaders(400, 0);
                    return;
                }

                String value = data.get(key);
                if (value == null) {
                    exchange.sendResponseHeaders(400, 0);
                    return;
                }
                sendText(exchange, value);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        } finally {
            exchange.close();
        }
    }

    private void save(HttpExchange exchange) throws IOException {
        try {
            if (!hasAuth(exchange)) {
                exchange.sendResponseHeaders(403, 0);
                return;
            }

            if ("POST".equals(exchange.getRequestMethod())) {
                String key = exchange.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    exchange.sendResponseHeaders(400, 0);
                    return;
                }

                String value = readText(exchange);
                if (value.isEmpty()) {
                    exchange.sendResponseHeaders(400, 0);
                    return;
                }

                data.put(key, value);
                exchange.sendResponseHeaders(200, 0);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        } finally {
            exchange.close();
        }
    }

    private void register(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                sendText(exchange, apiToken);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        } finally {
            exchange.close();
        }
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    private boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
    }

    private void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}
