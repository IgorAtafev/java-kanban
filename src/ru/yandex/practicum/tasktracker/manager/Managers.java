package ru.yandex.practicum.tasktracker.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.util.TaskAdapter;

public class Managers {
    /**
     * Returns a default task manager
     * @return task manager
     */
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    /**
     * Returns a default history manager
     * @return history manager
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getDefaultGson() {
        return new Gson();
    }

    public static Gson getTaskGson(TaskManager taskManager) {
        return new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskAdapter(taskManager))
                .registerTypeAdapter(Epic.class, new TaskAdapter(taskManager))
                .registerTypeAdapter(SubTask.class, new TaskAdapter(taskManager))
                .create();
    }

    public static Gson getEpicGson(TaskManager taskManager) {
        return new GsonBuilder()
                .registerTypeAdapter(SubTask.class, new TaskAdapter(taskManager))
                .create();
    }

    public static Gson getSubTaskGson(TaskManager taskManager) {
        return new GsonBuilder()
                .registerTypeAdapter(Epic.class, new TaskAdapter(taskManager))
                .create();
    }
}