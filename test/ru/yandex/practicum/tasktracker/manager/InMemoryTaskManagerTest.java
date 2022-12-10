package ru.yandex.practicum.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Status;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest {
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;
    private SubTask subTask3;

    private final TaskManager taskManager = new InMemoryTaskManager();

    @BeforeEach
    void setUp() {
        task1 = createTask("Задача1", "Описание задачи");
        taskManager.createTask(task1);
        task2 = createTask("Задача2", "Описание задачи");
        taskManager.createTask(task2);
        epic1 = createEpic("Эпик1", "Описание эпика");
        taskManager.createEpic(epic1);
        epic2 = createEpic("Эпик2", "Описание эпика");
        taskManager.createEpic(epic2);
        subTask1 = createSubTask("Подзадача1", "Описание подзадачи", epic1);
        taskManager.createSubTask(subTask1);
        subTask2 = createSubTask("Подзадача2", "Описание подзадачи", epic1);
        taskManager.createSubTask(subTask2);
        subTask3 = createSubTask("Подзадача3", "Описание подзадачи", epic2);
        taskManager.createSubTask(subTask3);
    }

    @Test
    void getTasks_shouldReturnListTasks() {
        List<Task> expected = List.of(task1, task2);
        List<Task> actual = taskManager.getTasks();

        assertEquals(expected, actual);
    }

    @Test
    void getEpics_shouldReturnListEpics() {
        List<Epic> expected = List.of(epic1, epic2);
        List<Epic> actual = taskManager.getEpics();

        assertEquals(expected, actual);
    }

    @Test
    void getSubTasks_shouldReturnListSubTasks() {
        List<SubTask> expected = List.of(subTask1, subTask2, subTask3);
        List<SubTask> actual = taskManager.getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void getSubTasksByEpic_shouldReturnListSubTasksByEpic() {
        List<SubTask> expected = List.of(subTask1, subTask2);
        List<SubTask> actual = taskManager.getSubTasksByEpic(epic1.getId());

        assertEquals(expected, actual);
    }

    @Test
    void updateSubTask_shouldChangeEpicStatusToInNew_ifStatusOfAllSubtasksChangedToInNew() {
        subTask1.setStatus(Status.NEW);
        subTask2.setStatus(Status.NEW);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);

        assertTrue(taskManager.getEpicById(subTask1.getEpic().getId()).getStatus() == Status.NEW);
    }

    @Test
    void updateSubTask_shouldChangeEpicStatusToInProgress_ifStatusOfOneSubtaskChangedToInProgress() {
        subTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);

        assertTrue(taskManager.getEpicById(subTask1.getEpic().getId()).getStatus() == Status.IN_PROGRESS);
    }

    @Test
    void updateSubTask_shouldChangeEpicStatusToDone_ifStatusOfAllSubtasksChangedToInDone() {
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);

        assertTrue(taskManager.getEpicById(subTask1.getEpic().getId()).getStatus() == Status.DONE);
    }


    private static Task createTask(String name, String description) {
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setStatus(Status.NEW);
        return task;
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