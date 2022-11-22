package ru.yandex.practicum.tasktracker.manager;

import ru.yandex.practicum.tasktracker.model.Task;

import java.util.List;
import java.util.Set;

public interface HistoryManager {
    /**
     * Adds a task to history
     * @param task
     */
    void add(Task task);

    /**
     * Removes a task by id
     * @param id
     */
    void remove(int id);

    /**
     * Removes a task by ids
     * @param ids
     */
    void removeAll(Set<Integer> ids);

    /**
     * Returns the task history
     * @return task history
     */
    List<Task> getHistory();
}