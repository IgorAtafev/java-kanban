package ru.yandex.practicum.tasktracker.manager;

public class Managers {
    /**
     * Returns a default task manager
     * @return task manager
     */
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    /**
     * Returns a default history manager
     * @return history manager
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}