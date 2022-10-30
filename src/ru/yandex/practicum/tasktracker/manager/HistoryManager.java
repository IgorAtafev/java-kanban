package ru.yandex.practicum.tasktracker.manager;

import ru.yandex.practicum.tasktracker.model.Task;

import java.util.List;

public interface HistoryManager {
    int HISTORY_SIZE = 10;

    List<Task> getHistory();

    /**
     * Adds a task to history
     * @param task
     */
    void add(Task task);
}