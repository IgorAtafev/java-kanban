package ru.yandex.practicum.tasktracker.manager;

/**
 * Utilitarian class.
 * Creates a task manager, selects the desired implementation of TaskManager
 */
public class Managers {
    /**
     * Returns a task manager
     * @return task manager
     */
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}