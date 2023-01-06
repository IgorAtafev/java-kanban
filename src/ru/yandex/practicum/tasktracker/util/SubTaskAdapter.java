package ru.yandex.practicum.tasktracker.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.tasktracker.model.SubTask;

import java.io.IOException;

public class SubTaskAdapter extends TypeAdapter<SubTask> {
    @Override
    public void write(JsonWriter jsonWriter, SubTask subTask) throws IOException {
        jsonWriter.value(subTask.getId());
    }

    @Override
    public SubTask read(JsonReader jsonReader) throws IOException {
        return null;
    }
}