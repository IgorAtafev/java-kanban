package ru.yandex.practicum.tasktracker.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.tasktracker.server.KVServer;

import java.io.IOException;

class HttpTaskManagerTest extends InMemoryTaskManagerTest {
    private static final String URL = "http://localhost:" + KVServer.PORT;

    private KVServer kvServer;

    @Override
    protected HttpTaskManager createTaskManager() {
        return HttpTaskManager.load(URL);
    }

    @BeforeEach
    @Override
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        super.setUp();
    }

    @AfterEach
    void serverStop() throws IOException {
        kvServer.stop();
    }
}