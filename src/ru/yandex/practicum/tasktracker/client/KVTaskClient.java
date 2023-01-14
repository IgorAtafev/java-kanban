package ru.yandex.practicum.tasktracker.client;

import ru.yandex.practicum.tasktracker.manager.exception.HttpRequestSendException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String url;
    private final String token;

    public KVTaskClient(String url) {
        this.url = url;
        token = register();
    }

    /**
     * Saves the state of the task manager via a POST request /save/<key>?API_TOKEN=
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create(url + "/save/" + key + "/?API_TOKEN=" + token);
            HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(value);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(body)
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException | IllegalArgumentException e) {
            throw new HttpRequestSendException("An error occurred while executing the save manager state request", e);
        }
    }

    /**
     * Returns the state of the task manager via a GET request /load/<key>?API_TOKEN=
     * @param key
     * @return state of the task manager
     */
    public String load(String key) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create(url + "/load/" + key + "/?API_TOKEN=" + token);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (IOException | InterruptedException | IllegalArgumentException e) {
            throw new HttpRequestSendException("An error occurred while executing a manager state restore request", e);
        }
    }

    private String register() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create(url + "/register/");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (IOException | InterruptedException | IllegalArgumentException e) {
            throw new HttpRequestSendException("An error occurred while executing the token registration request", e);
        }
    }
}