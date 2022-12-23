package ru.yandex.practicum.tasktracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.manager.InMemoryTaskManager;
import ru.yandex.practicum.tasktracker.manager.TaskManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;

    private final TaskManager taskManager = new InMemoryTaskManager();

    @BeforeEach
    void setUp() {
        createTestTasks();
    }

    @Test
    void getStatus_shouldChangeEpicStatusToNew_ifThereAreNoSubtasks() {
        assertTrue(taskManager.getEpicById(epic2.getId()).getStatus() == Status.NEW);
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

    //@Test
    //void getStartTime_

    private void createTestTasks() {
        epic1 = createEpic("Эпик1", "Описание эпика");
        taskManager.createEpic(epic1);
        epic2 = createEpic("Эпик2", "Описание эпика");
        taskManager.createEpic(epic2);
        subTask1 = createSubTask("Подзадача1", "Описание подзадачи", epic1);
        taskManager.createSubTask(subTask1);
        subTask2 = createSubTask("Подзадача2", "Описание подзадачи", epic1);
        taskManager.createSubTask(subTask2);
    }

    private Epic createEpic(String name, String description) {
        Epic epic = new Epic();
        epic.setName(name);
        epic.setDescription(description);
        return epic;
    }

    private SubTask createSubTask(String name, String description, Epic epic) {
        SubTask subTask = new SubTask();
        subTask.setName(name);
        subTask.setDescription(description);
        subTask.setStatus(Status.NEW);
        subTask.setEpic(epic);
        return subTask;
    }
}