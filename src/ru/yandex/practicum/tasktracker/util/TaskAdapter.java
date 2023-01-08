package ru.yandex.practicum.tasktracker.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.tasktracker.manager.TaskManager;
import ru.yandex.practicum.tasktracker.model.Task;

import java.io.IOException;

public class TaskAdapter extends TypeAdapter<Task> {
    private final TaskManager taskManager;

    public TaskAdapter(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void write(JsonWriter jsonWriter, Task task) throws IOException {
        jsonWriter.value(task.getId());
    }

    @Override
    public Task read(JsonReader jsonReader) throws IOException {
        final int nextInt = jsonReader.nextInt();
        return taskManager.getTasks().stream()
                .filter(task -> nextInt == task.getId())
                .findFirst()
                .orElse(null);
    }
}