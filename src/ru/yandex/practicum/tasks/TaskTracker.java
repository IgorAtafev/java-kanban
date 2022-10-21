package ru.yandex.practicum.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskTracker {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    private int counterId = 0;

    /**
     * Gets a list of all tasks
     * @return
     */
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Gets a list of all epics
     * @return
     */
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Gets a task by id
     * @param id
     * @return task or null if there was no mapping for id
     */
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    /**
     * Gets an epic by id
     * @param id
     * @return epic or null if there was no mapping for id
     */
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    /**
     * Deletes all tasks
     */
    public void deleteTasks() {
        tasks.clear();
    }

    /**
     * Deletes all epics
     */
    public void deleteEpics() {
        epics.clear();
    }

    /**
     * Deletes a task by id
     * @param id
     * @return task or null if there was no mapping for id
     */
    public Task deleteTaskById(int id) {
        return tasks.remove(id);
    }

    /**
     * Deletes an epic by id
     * @param id
     * @return epic or null if there was no mapping for id
     */
    public Epic deleteEpicById(int id) {
        return epics.remove(id);
    }

    //public void
}