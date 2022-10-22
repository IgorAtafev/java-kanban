package ru.yandex.practicum.taskTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskTracker {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    private int nextTaskId = 0;
    private int nextEpicId = 0;
    private int nextSubTaskId = 0;

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
     * Gets a subtask by id
     * @param id
     * @return subtask or null if there was no mapping for id
     */
    public SubTask getSubTaskById(int id) {
        for (Epic epic : epics.values()) {
            SubTask subTask = epic.getSubTasks().get(id);
            if (subTask != null) {
                return subTask;
            }
        }

        return null;
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
     * Deletes a subtask by id
     * @param id
     * @return subtask or null if there was no mapping for id
     */
    public SubTask deleteSubTaskById(int id) {
        for (Epic epic : epics.values()) {
            SubTask subTask = epic.getSubTasks().get(id);
            if (subTask != null) {
                return epic.getSubTasks().remove(id);
            }
        }

        return null;
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

        Task newTask = new Task(task.getName(), task.getDescription(), ++nextTaskId);
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
    public Epic createEpic(Epic epic) {
        if (epic == null) {
            return null;
        }

        Epic newEpic = new Epic(epic.getName(), epic.getDescription(), ++nextEpicId);
        if (epics.containsKey(newEpic.getId())) {
            return null;
        }
        epics.put(newEpic.getId(), newEpic);

        return newEpic;
    }

    /**
     * Creates a new subtask
     * @param subTask
     * @return subtask or null if there was mapping for id
     */
    public SubTask createSubTask(SubTask subTask) {
        if (subTask == null) {
            return null;
        }

        Epic epic = epics.get(subTask.getEpicId());
        if (epic == null) {
            return null;
        }

        SubTask newSubTask = new SubTask(subTask.getName(), subTask.getDescription(), ++nextSubTaskId, epic.getId());
        if (epic.getSubTasks().containsKey(newSubTask.getId())) {
            return null;
        }
        epic.getSubTasks().put(newSubTask.getId(), newSubTask);

        return newSubTask;
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
    public Epic updateEpic(Epic epic) {
        if (epic == null) {
            return null;
        }

        Epic originalEpic = epics.get(epic.getId());
        if (originalEpic == null) {
            return null;
        }

        originalEpic.setName(epic.getName());
        originalEpic.setDescription(epic.getDescription());

        return originalEpic;
    }

    /**
     * Updates the subtask
     * @param subTask
     * @return subtask or null if there was no mapping for id
     */
    /*public SubTask updateSubTask(SubTask subTask) {
        if (subTask == null) {
            return null;
        }

        for (Epic epic : epics.values()) {
            if (epic.getSubTasks().containsKey())
        }
        Task originalTask = tasks.get(task.getId());
        if (tasks.get(task.getId()) == null) {
            return null;
        }

        originalTask.setName(task.getName());
        originalTask.setDescription(task.getDescription());
        originalTask.setStatus(task.getStatus());

        return originalTask;
    }*/
}