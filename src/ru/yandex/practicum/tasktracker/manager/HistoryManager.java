package ru.yandex.practicum.tasktracker.manager;

import ru.yandex.practicum.tasktracker.model.Task;

import java.util.List;

public interface HistoryManager {
    /**
     * Adds a task to history
     * @param task
     */
    void add(Task task);

    /**
     * Removes a task to history by index
     * @param id
     */
    void remove(int id);

    /**
     * Returns the task view history
     * @return task view history
     */
    List<Task> getHistory();
}