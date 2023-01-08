package ru.yandex.practicum.tasktracker.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.tasktracker.manager.TaskManager;
import ru.yandex.practicum.tasktracker.model.Epic;

import java.io.IOException;

public class EpicAdapter extends TypeAdapter<Epic> {
    private final TaskManager taskManager;

    public EpicAdapter(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void write(JsonWriter jsonWriter, Epic epic) throws IOException {
        jsonWriter.value(epic.getId());
    }

    @Override
    public Epic read(JsonReader jsonReader) throws IOException {
        final int nextInt = jsonReader.nextInt();
        return taskManager.getEpics().stream()
                .filter(task -> nextInt == task.getId())
                .findFirst()
                .orElse(null);
    }
}