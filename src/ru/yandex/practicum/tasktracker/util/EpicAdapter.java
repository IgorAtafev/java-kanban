package ru.yandex.practicum.tasktracker.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.tasktracker.model.Epic;

import java.io.IOException;

public class EpicAdapter extends TypeAdapter<Epic> {
    @Override
    public void write(JsonWriter jsonWriter, Epic epic) throws IOException {
        jsonWriter.value(epic.getId());
    }

    @Override
    public Epic read(JsonReader jsonReader) throws IOException {
        return null;
    }
}