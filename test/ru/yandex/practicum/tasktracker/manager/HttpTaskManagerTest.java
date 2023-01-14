package ru.yandex.practicum.tasktracker.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.manager.exception.HttpRequestSendException;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Status;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.server.KVServer;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void serverStop() {
        kvServer.stop();
    }

    @Test
    void load_shouldThrowAnException_ifTheServerIsNotFound() {
        HttpRequestSendException exception = assertThrows(
                HttpRequestSendException.class,
                () -> HttpTaskManager.load("http://not_found")
        );
    }

    @Test
    void load_shouldLoadTasksFromServerAndRestoreTaskListsAndHistory() {
        HttpTaskManager taskManager = HttpTaskManager.load(URL);

        assertTrue(taskManager.getTasks().isEmpty());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubTasks().isEmpty());
        assertTrue(taskManager.getHistory().isEmpty());
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());

        task1.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        task1.setDuration(Duration.ofMinutes(30));
        taskManager.createTask(task1);

        task2.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 40));
        task2.setDuration(Duration.ofMinutes(15));
        taskManager.createTask(task2);

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        subTask1.setStartTime(LocalDateTime.of(2022, 12, 22, 10, 30));
        subTask1.setDuration(Duration.ofMinutes(20));
        taskManager.createSubTask(subTask1);

        subTask2.setStartTime(LocalDateTime.of(2022, 12, 22, 10, 50));
        subTask2.setDuration(Duration.ofMinutes(10));
        taskManager.createSubTask(subTask2);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());
        taskManager.getEpicById(epic2.getId());

        task1.setStartTime(LocalDateTime.of(2022, 12, 22, 9, 0));
        task1.setDuration(Duration.ofMinutes(40));
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);

        subTask2.setStartTime(LocalDateTime.of(2022, 12, 22, 10, 15));
        subTask2.setDuration(Duration.ofMinutes(15));
        subTask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask2);

        epic1.setName("Обновленный эпик");
        epic1.setDescription("Описание обновленного эпика");
        taskManager.updateEpic(epic1);

        taskManager.deleteTaskById(task1.getId());
        taskManager.createSubTask(subTask3);
        taskManager.getSubTaskById(subTask3.getId());
        taskManager.deleteEpicById(epic1.getId());
        taskManager.deleteSubTaskById(subTask3.getId());
        taskManager.deleteTasks();

        Task task4 = createTask("Задача4", "Описание задачи");
        task4.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        task4.setDuration(Duration.ofMinutes(30));
        task4.setStatus(Status.NEW);
        taskManager.createTask(task4);

        taskManager.deleteEpics();

        Epic epic3 = createEpic("Эпик3", "Описание эпика");
        taskManager.createEpic(epic3);
        Epic epic4 = createEpic("Эпик4", "Описание эпика");
        taskManager.createEpic(epic4);

        SubTask subTask4 = createSubTask("Подзадача4", "Описание подзадачи", epic4);
        subTask4.setStartTime(LocalDateTime.of(2022, 12, 22, 17, 0));
        subTask4.setDuration(Duration.ofMinutes(30));
        subTask4.setStatus(Status.DONE);
        taskManager.createSubTask(subTask4);
        SubTask subTask5 = createSubTask("Подзадача5", "Описание подзадачи", epic4);
        subTask5.setStartTime(LocalDateTime.of(2022, 12, 22, 17, 30));
        subTask5.setDuration(Duration.ofMinutes(30));
        subTask5.setStatus(Status.DONE);
        taskManager.createSubTask(subTask5);

        taskManager.getTaskById(task4.getId());
        taskManager.getEpicById(epic4.getId());
        taskManager.getSubTaskById(subTask5.getId());

        List<Task> expectedTasks = taskManager.getTasks();
        List<Epic> expectedEpics = taskManager.getEpics();
        List<SubTask> expectedSubTasks = taskManager.getSubTasks();
        List<Task> expectedHistory = taskManager.getHistory();
        List<Task> expectedPrioritizedTasks = List.copyOf(taskManager.getPrioritizedTasks());

        taskManager = HttpTaskManager.load(URL);

        List<Task> actualTasks = taskManager.getTasks();
        assertEquals(expectedTasks, actualTasks);

        List<Epic> actualEpics = taskManager.getEpics();
        assertEquals(expectedEpics, actualEpics);

        List<SubTask> actualSubTasks = taskManager.getSubTasks();
        assertEquals(expectedSubTasks, actualSubTasks);

        List<Task> actualHistory = taskManager.getHistory();
        assertEquals(expectedHistory, actualHistory);

        List<Task> actualPrioritizedTasks = List.copyOf(taskManager.getPrioritizedTasks());
        assertEquals(expectedPrioritizedTasks, actualPrioritizedTasks);
    }
}