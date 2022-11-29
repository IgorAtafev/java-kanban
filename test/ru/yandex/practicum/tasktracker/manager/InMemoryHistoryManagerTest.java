package ru.yandex.practicum.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Status;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;

    private final HistoryManager historyManager = new InMemoryHistoryManager();

    @BeforeEach
    void setUp() {
        task1 = createTask(1, "Задача1", Status.NEW);
        task2 = createTask(2, "Задача2", Status.IN_PROGRESS);
        epic1 = createEpic(3, "Эпик1");
        epic2 = createEpic(4, "Эпик2");
        subTask1 = createSubTask(5, "Подзадача1", Status.DONE, epic1);
        subTask2 = createSubTask(6, "Подзадача2", Status.DONE, epic1);
    }

    @Test
    void getHistory_shouldReturnEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void add_shouldSaveTaskToHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.add(subTask1);
        historyManager.add(subTask2);

        List<Task> expected = List.of(task1, task2, epic1, subTask1, subTask2);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void add_shouldNotKeepDuplicates() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.add(epic1);

        List<Task> expected = List.of(task1, task2, epic1);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void add_shouldMoveTaskToTheEnd_ifTaskAlreadyExistsInHistory() {
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subTask1);
        historyManager.add(epic2);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(subTask1);

        List<Task> expected = List.of(epic1, epic2, task1, subTask1);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void remove_shouldRemoveTaskFromBeginningHistory() {
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(task2);
        historyManager.add(epic2);
        historyManager.add(subTask2);
        historyManager.add(subTask1);

        historyManager.remove(1);

        List<Task> expected = List.of(epic1, task2, epic2, subTask2, subTask1);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void remove_shouldRemoveTaskFromMiddleHistory() {
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(task2);
        historyManager.add(epic2);
        historyManager.add(subTask2);
        historyManager.add(subTask1);

        historyManager.remove(4);

        List<Task> expected = List.of(task1, epic1, task2, subTask2, subTask1);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void remove_shouldRemoveTaskFromEndHistory() {
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(task2);
        historyManager.add(epic2);
        historyManager.add(subTask2);
        historyManager.add(subTask1);

        historyManager.remove(5);

        List<Task> expected = List.of(task1, epic1, task2, epic2, subTask2);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void removeAll_shouldRemoveTasksFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(subTask2);
        historyManager.add(epic2);
        historyManager.add(epic1);

        historyManager.removeAll(Set.of(1, 6, 3));

        List<Task> expected = List.of(task2, epic2);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    private static Task createTask(int id, String name, Status status) {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setStatus(status);

        return task;
    }

    private static Epic createEpic(int id, String name) {
        Epic epic = new Epic();
        epic.setId(id);
        epic.setName(name);

        return epic;
    }

    private static SubTask createSubTask(int id, String name, Status status, Epic epic) {
        SubTask subTask = new SubTask();
        subTask.setId(id);
        subTask.setName(name);
        subTask.setStatus(status);
        subTask.setEpic(epic);

        return subTask;
    }
}