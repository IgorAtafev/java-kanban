package ru.yandex.practicum.tasktracker.client;

import ru.yandex.practicum.tasktracker.server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private static final String URL = "http://localhost:" + KVServer.PORT;

    private final String token;
    private final HttpClient client;

    public KVTaskClient() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();
        token = register();
    }

    /**
     * Saves the state of the task manager via a POST request /save/<key>?API_TOKEN=
     * @param key
     * @param value
     */
    public void put(String key, String value) throws IOException, InterruptedException {
        URI uri = URI.create(URL + "/save/" + key + "/?API_TOKEN=" + token);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(value);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Returns the state of the task manager via a GET request /load/<key>?API_TOKEN=
     * @param key
     * @return the state of the task manager
     */
    public String load(String key) throws IOException, InterruptedException {
        URI uri = URI.create(URL + "/load/" + key + "/?API_TOKEN=" + token);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    private String register() throws IOException, InterruptedException {
        URI uri = URI.create(URL + "/register/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}