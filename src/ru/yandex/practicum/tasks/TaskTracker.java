package ru.yandex.practicum.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskTracker {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    private int nextId = 0;

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

    /**
     * Creates a new task
     * @param task
     * @return task or null if there was mapping for id
     */
    public Task createTask(Task task) {
        if (task == null) {
            return null;
        }

        Task newTask = new Task(task.getName(), task.getDescription(), ++nextId);
        if (tasks.containsKey(newTask.getId())) {
            return null;
        }
        tasks.put(newTask.getId(), newTask);

        return newTask;
    }

    /**
     * Creates a new epic
     * @param epic
     * @return epic or null if there was mapping for id
     */
    public Task createEpic(Epic epic) {
        if (epic == null) {
            return null;
        }

        Epic newEpic = new Epic(epic.getName(), epic.getDescription(), ++nextId);
        if (epics.containsKey(newEpic.getId())) {
            return null;
        }
        epics.put(newEpic.getId(), newEpic);

        return newEpic;
    }

    /**
     * Updates the task
     * @param task
     * @return task or null if there was no mapping for id
     */
    public Task updateTask(Task task) {
        if (task == null) {
            return null;
        }

        Task originalTask = tasks.get(task.getId());
        if (originalTask == null) {
            return null;
        }

        originalTask.setName(task.getName());
        originalTask.setDescription(task.getDescription());
        originalTask.setStatus(task.getStatus());

        return originalTask;
    }

    /**
     * Updates the epic
     * @param epic
     * @return epic or null if there was no mapping for id
     */
    public Task updateEpic(Epic epic) {
        if (epic == null) {
            return null;
        }

        Epic originalEpic = epics.get(epic.getId());
        if (originalEpic == null) {
            return null;
        }

        originalEpic.setName(epic.getName());
        originalEpic.setDescription(epic.getDescription());
        originalEpic.setStatus(epic.getStatus());

        return originalEpic;
    }
}