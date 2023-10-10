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
     * @param taskId
     */
    void remove(int taskId);

    /**
     * Removes a task by ids
     * @param taskIds
     */
    void removeAll(Set<Integer> taskIds);

    /**
     * Returns the task history
     * @return task history
     */
    List<Task> getHistory();
}
