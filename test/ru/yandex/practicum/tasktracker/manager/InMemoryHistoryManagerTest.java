package ru.yandex.practicum.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Epic;
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
        task1 = createTask(1);
        task2 = createTask(2);
        epic1 = createEpic(3);
        epic2 = createEpic(4);
        subTask1 = createSubTask(5, epic1);
        subTask2 = createSubTask(6, epic1);
    }

    @Test
    void getHistory_shouldReturnEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void add_shouldSaveTaskToHistory() {
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subTask1);

        List<Task> expected = List.of(task1, epic1, subTask1);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void add_shouldNotKeepDuplicates() {
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.add(epic1);
        historyManager.add(subTask1);
        historyManager.add(subTask1);

        List<Task> expected = List.of(task1, task2, epic1, subTask1);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void add_shouldMoveTaskToTheEnd_ifTaskAlreadyExistsInHistory() {
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subTask1);
        historyManager.add(task1);

        List<Task> expected = List.of(epic1, subTask1, task1);
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

        historyManager.remove(task1.getId());

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

        historyManager.remove(epic2.getId());

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

        historyManager.remove(subTask1.getId());

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
        historyManager.add(epic2);

        historyManager.removeAll(Set.of(task1.getId(), subTask2.getId(), epic1.getId()));

        List<Task> expected = List.of(task2, epic2);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    private static Task createTask(int id) {
        Task task = new Task();
        task.setId(id);
        return task;
    }

    private static Epic createEpic(int id) {
        Epic epic = new Epic();
        epic.setId(id);
        return epic;
    }

    private static SubTask createSubTask(int id, Epic epic) {
        SubTask subTask = new SubTask();
        subTask.setId(id);
        subTask.setEpic(epic);
        return subTask;
    }
}