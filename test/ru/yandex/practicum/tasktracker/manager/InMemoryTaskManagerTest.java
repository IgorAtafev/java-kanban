package ru.yandex.practicum.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Status;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;

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
    }

    @Test
    void getHistory_shouldReturnEmptyHistory() {
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void getTasks_shouldCheckForNull() {
        List<Task> actual = taskManager.getTasks();
        assertNotNull(actual);
    }

    @Test
    void getTasks_shouldReturnListTasks() {
        List<Task> expected = List.of(task1, task2);
        List<Task> actual = taskManager.getTasks();

        assertEquals(expected, actual);
    }

    @Test
    void getEpics_shouldCheckForNull() {
        List<Epic> actual = taskManager.getEpics();
        assertNotNull(actual);
    }

    @Test
    void getEpics_shouldReturnListEpics() {
        List<Epic> expected = List.of(epic1, epic2);
        List<Epic> actual = taskManager.getEpics();

        assertEquals(expected, actual);
    }

    @Test
    void getSubTasks_shouldCheckForNull() {
        List<SubTask> actual = taskManager.getSubTasks();
        assertNotNull(actual);
    }

    @Test
    void getSubTasks_shouldReturnListSubTasks() {
        List<SubTask> expected = List.of(subTask1, subTask2);
        List<SubTask> actual = taskManager.getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void getSubTasksByEpic_shouldCheckForNull() {
        List<SubTask> actual = taskManager.getSubTasksByEpic(epic1.getId());
        assertNotNull(actual);
    }

    @Test
    void getSubTasksByEpic_shouldReturnListSubTasksByEpic() {
        List<SubTask> expected = List.of(subTask1, subTask2);
        List<SubTask> actual = taskManager.getSubTasksByEpic(epic1.getId());

        assertEquals(expected, actual);
    }

    @Test
    void getTaskById_shouldReturnTaskById() {
        Task task = taskManager.getTaskById(task1.getId());
        assertEquals(task1, task);
    }

    @Test
    void getTaskById_shouldAddTaskToHistory() {
        taskManager.getTaskById(task1.getId());
        List<Task> expected = List.of(task1);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void getEpicById_shouldReturnEpicById() {
        Epic epic = taskManager.getEpicById(epic1.getId());
        assertEquals(epic1, epic);
    }

    @Test
    void getEpicById_shouldAddTaskToHistory() {
        taskManager.getEpicById(epic1.getId());
        List<Task> expected = List.of(epic1);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void getSubTaskById_shouldReturnSubTaskById() {
        SubTask subTask = taskManager.getSubTaskById(subTask1.getId());
        assertEquals(subTask1, subTask);
    }

    @Test
    void getSubTaskById_shouldAddTaskToHistory() {
        taskManager.getSubTaskById(subTask1.getId());
        List<Task> expected = List.of(subTask1);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteTaskById_shouldRemoveTheTaskFromTheList() {
        taskManager.deleteTaskById(task1.getId());
        List<Task> expected = List.of(task2);
        List<Task> actual = taskManager.getTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteTaskById_shouldRemoveTaskFromHistory() {
        taskManager.addHistory(task1);
        taskManager.deleteTaskById(task1.getId());
        List<Task> expected = List.of();
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpicById_shouldRemoveTheEpicFromTheList() {
        taskManager.deleteEpicById(epic1.getId());
        List<Epic> expected = List.of(epic2);
        List<Epic> actual = taskManager.getEpics();

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpicById_shouldRemoveEpicFromHistory() {
        taskManager.addHistory(epic1);
        taskManager.deleteEpicById(epic1.getId());
        List<Task> expected = List.of();
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTaskById_shouldRemoveTheSubTaskFromTheList() {
        taskManager.deleteSubTaskById(subTask1.getId());
        List<SubTask> expected = List.of(subTask2);
        List<SubTask> actual = taskManager.getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTaskById_shouldRemoveSubTaskFromHistory() {
        taskManager.addHistory(subTask1);
        taskManager.deleteSubTaskById(subTask1.getId());
        List<Task> expected = List.of();
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteTasks_shouldRemoveAllTasksFromTheList() {
        taskManager.deleteTasks();
        List<Task> expected = List.of();
        List<Task> actual = taskManager.getTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteTasks_shouldRemoveAllTasksFromHistory() {
        taskManager.addHistory(task1);
        taskManager.addHistory(task2);
        taskManager.addHistory(epic1);
        taskManager.addHistory(epic2);
        taskManager.addHistory(subTask1);
        taskManager.addHistory(subTask2);
        taskManager.deleteTasks();
        List<Task> expected = List.of(epic1, epic2, subTask1, subTask2);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpics_shouldRemoveAllEpicsFromTheList() {
        taskManager.deleteEpics();
        List<Epic> expected = List.of();
        List<Epic> actual = taskManager.getEpics();

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpics_shouldRemoveAllEpicsFromHistory() {
        taskManager.addHistory(task1);
        taskManager.addHistory(task2);
        taskManager.addHistory(epic1);
        taskManager.addHistory(epic2);
        taskManager.addHistory(subTask1);
        taskManager.addHistory(subTask2);
        taskManager.deleteEpics();
        List<Task> expected = List.of(task1, task2);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTasks_shouldRemoveAllSubTasksFromTheList() {
        taskManager.deleteSubTasks();
        List<SubTask> expected = List.of();
        List<SubTask> actual = taskManager.getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTasks_shouldRemoveAllSubTasksFromHistory() {
        taskManager.addHistory(task1);
        taskManager.addHistory(task2);
        taskManager.addHistory(epic1);
        taskManager.addHistory(epic2);
        taskManager.addHistory(subTask1);
        taskManager.addHistory(subTask2);
        taskManager.deleteSubTasks();
        List<Task> expected = List.of(task1, task2, epic1, epic2);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void updateSubTask_shouldChangeEpicStatusToNew_ifStatusOfAllSubtasksChangedToNew() {
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
    void updateSubTask_shouldChangeEpicStatusToDone_ifStatusOfAllSubtasksChangedToDone() {
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