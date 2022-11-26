package ru.yandex.practicum.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {
    private Task task1;
    private Task task2;
    private Task task3;

    private final HistoryManager historyManager = new InMemoryHistoryManager();

    @BeforeEach
    void setUp() {
        task1 = createTask(1);
        task2 = createTask(2);
        task3 = createTask(3);
    }

    @Test
    void getHistory_shouldReturnEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void add_shouldSaveTaskToHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> expected = List.of(task1, task2, task3);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void add_shouldNotKeepDuplicates() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task3);

        List<Task> expected = List.of(task1, task2, task3);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void add_shouldMoveTaskToTheEnd_ifTaskAlreadyExistsInHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task1);

        List<Task> expected = List.of(task2, task3, task1);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void remove_shouldRemoveTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);

        List<Task> expected = List.of(task1, task3);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void removeAll_shouldRemoveTasksFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.removeAll(Set.of(1, 3));

        List<Task> expected = List.of(task2);
        List<Task> actual = historyManager.getHistory();

        assertEquals(expected, actual);
    }

    private static Task createTask(int id) {
        Task task = new Task();
        task.setId(id);
        return task;
    }
}