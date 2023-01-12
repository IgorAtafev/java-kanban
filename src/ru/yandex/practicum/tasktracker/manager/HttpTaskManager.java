package ru.yandex.practicum.tasktracker.manager;

import com.google.gson.Gson;
import ru.yandex.practicum.tasktracker.client.KVTaskClient;
import ru.yandex.practicum.tasktracker.manager.exception.ManagerSaveException;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTaskManager {
    private static final String TASKS_KEY = "tasks";
    private static final String EPICS_KEY = "epics";
    private static final String SUBTASKS_KEY = "subtasks";
    private static final String HISTORY_KEY = "history";

    private final String url;

    private final KVTaskClient client;

    private final Gson defaultGson;
    private final Gson taskGson;
    private final Gson epicGson;
    private final Gson subTaskGson;

    private HttpTaskManager() {
        this("http://localhost:8078");
    }

    private HttpTaskManager(String url) {
        this.url = url;

        try {
            client = new KVTaskClient(url);

            defaultGson = Managers.getDefaultGson();
            taskGson = Managers.getTaskGson(this);
            epicGson = Managers.getEpicGson(this);
            subTaskGson = Managers.getSubTaskGson(this);
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("An error occurred while executing the request", e);
        }
    }

    @Override
    protected void save() {
        String tasks = defaultGson.toJson(getTasks());
        String epics = epicGson.toJson(getEpics());
        String subTasks = subTaskGson.toJson(getSubTasks());
        String history = taskGson.toJson(getHistory());

        try {
            client.put(TASKS_KEY, tasks);
            client.put(EPICS_KEY, epics);
            client.put(SUBTASKS_KEY, subTasks);
            client.put(HISTORY_KEY, history);
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Server write error", e);
        }
    }
}