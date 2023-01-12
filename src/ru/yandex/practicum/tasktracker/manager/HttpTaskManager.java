package ru.yandex.practicum.tasktracker.manager;

import ru.yandex.practicum.tasktracker.client.KVTaskClient;
import ru.yandex.practicum.tasktracker.manager.exception.ManagerSaveException;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTaskManager {
    private final String url;

    private final KVTaskClient client;

    private HttpTaskManager(String url) {
        this.url = url;

        try {
            client = new KVTaskClient(url);
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("An error occurred while executing the request", e);
        }
    }
}