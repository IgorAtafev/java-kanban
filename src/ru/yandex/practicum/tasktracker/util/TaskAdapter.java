package ru.yandex.practicum.tasktracker.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.io.IOException;

public class TaskAdapter extends TypeAdapter<Task> {
    @Override
    public void write(JsonWriter jsonWriter, Task task) throws IOException {
        jsonWriter.value(task.getId());
    }

    @Override
    public SubTask read(JsonReader jsonReader) throws IOException {
        return null;
    }
}