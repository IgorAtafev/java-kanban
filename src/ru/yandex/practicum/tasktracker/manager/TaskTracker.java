package ru.yandex.practicum.tasktracker.manager;

import ru.yandex.practicum.tasktracker.Status;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskTracker {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();

    private int nextTaskId = 0;
    private int nextEpicId = 0;
    private int nextSubTaskId = 0;

    /**
     * Gets a list of all tasks
     */
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Gets a list of all epics
     */
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Gets a list of all subtasks
     */
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    /**
     * Gets a list of all subtasks by epic
     * @param epic
     */
    public List<SubTask> getSubTasksByEpic(Epic epic) {
        return new ArrayList<>(epic.getSubTasks());
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
        return subTasks.get(id);
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
        subTasks.clear();
        epics.clear();
    }

    /**
     * Deletes all subtasks
     */
    public void deleteSubTasks() {
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
        }
        subTasks.clear();
    }

    /**
     * Deletes all subtasks by epic
     * @param epic
     */
    public void deleteSubTasksByEpic(Epic epic) {
        subTasks.values().removeAll(epic.getSubTasks());
        epic.getSubTasks().clear();
    }

    /**
     * Deletes a task by id
     * @param id
     */
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    /**
     * Deletes an epic by id
     * @param id
     */
    public void deleteEpicById(int id) {
        subTasks.values().removeAll(getEpicById(id).getSubTasks());
        epics.remove(id);
    }

    /**
     * Deletes a subtask by id
     * @param id
     */
    public void deleteSubTaskById(int id) {
        SubTask task = getSubTaskById(id);
        task.getEpic().getSubTasks().remove(task);
        subTasks.remove(id);
    }

    /**
     * Creates a new task
     * @param task
     */
    public void createTask(Task task) {
        task.setId(++nextTaskId);
        task.setStatus(Status.NEW);
        tasks.put(task.getId(), task);
    }

    /**
     * Creates a new epic
     * @param epic
     */
    public void createEpic(Epic epic) {
        epic.setId(++nextEpicId);
        epics.put(epic.getId(), epic);
    }

    /**
     * Creates a new subtask
     * @param subTask
     */
    public void createSubTask(SubTask subTask) {
        subTask.setId(++nextSubTaskId);
        subTask.setStatus(Status.NEW);
        subTask.getEpic().getSubTasks().add(subTask);
        subTasks.put(subTask.getId(), subTask);
    }

    /**
     * Updates the task
     * @param task
     */
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    /**
     * Updates the epic
     * @param epic
     */
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    /**
     * Updates the subtask
     * @param subTask
     */
    public void updateSubTask(SubTask subTask) {
        SubTask originalTask = getSubTaskById(subTask.getId());
        originalTask.getEpic().getSubTasks().remove(originalTask);
        subTask.getEpic().getSubTasks().add(subTask);
        subTasks.put(subTask.getId(), subTask);
    }
}