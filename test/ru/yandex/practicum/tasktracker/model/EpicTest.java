package ru.yandex.practicum.tasktracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.manager.InMemoryTaskManager;
import ru.yandex.practicum.tasktracker.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epic;
    private SubTask subTask1;
    private SubTask subTask2;

    private final TaskManager taskManager = new InMemoryTaskManager();

    @BeforeEach
    void setUp() {
        epic = createEpic("Эпик1", "Описание эпика");
        taskManager.createEpic(epic);
        subTask1 = createSubTask("Подзадача1", "Описание подзадачи", epic);
        taskManager.createSubTask(subTask1);
        subTask2 = createSubTask("Подзадача2", "Описание подзадачи", epic);
        taskManager.createSubTask(subTask2);
    }

    @Test
    void getStatus_shouldChangeEpicStatusToNew_ifStatusOfAllSubtasksChangedToNew() {
        subTask1.setStatus(Status.NEW);
        subTask2.setStatus(Status.NEW);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);

        assertTrue(taskManager.getEpicById(subTask1.getEpic().getId()).getStatus() == Status.NEW);
    }

    @Test
    void getStatus_shouldChangeEpicStatusToDone_ifStatusOfAllSubtasksChangedToDone() {
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);

        assertTrue(taskManager.getEpicById(subTask1.getEpic().getId()).getStatus() == Status.DONE);
    }

    @Test
    void getStatus_shouldChangeEpicStatusToInProgress_ifStatusOfOneSubtaskChangedToInProgress() {
        subTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);

        assertTrue(taskManager.getEpicById(subTask1.getEpic().getId()).getStatus() == Status.IN_PROGRESS);
    }

    @Test
    void getStatus_shouldChangeEpicStatusToInProgress_ifStatusOfAllSubtasksAreDifferentAndNotInProgress() {
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.NEW);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);

        assertTrue(taskManager.getEpicById(subTask1.getEpic().getId()).getStatus() == Status.IN_PROGRESS);
    }

    private static Epic createEpic(String name, String description) {
        Epic epic = new Epic();
        epic.setName(name);
        epic.setDescription(description);
        return epic;
    }

    private static SubTask createSubTask(String name, String description, Epic epic) {
        SubTask subTask = new SubTask();
        subTask.setName(name);
        subTask.setDescription(description);
        subTask.setStatus(Status.NEW);
        subTask.setEpic(epic);
        return subTask;
    }
}