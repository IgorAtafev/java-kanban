package ru.yandex.practicum.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {
    private final String fileToSave = "test/empty.csv";

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return FileBackedTaskManager.loadFromFile(fileToSave);
    }

    @BeforeEach
    void setUp() throws IOException {
        Files.writeString(Path.of("resources/" + fileToSave), "");
        super.setUp();
    }
}