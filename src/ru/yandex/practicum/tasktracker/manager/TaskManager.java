package ru.yandex.practicum.tasktracker.manager;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();

    private int nextTaskId = 0;

    /**
     * Returns a list of all tasks
     * @return list of all tasks
     */
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Returns a list of all epics
     * @return list of all epics
     */
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Returns a list of all subtasks
     * @return list of all subtasks
     */
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    /**
     * Returns a list of all subtasks by id epic
     * @param id
     * @return list of all subtasks by id epic
     */
    public List<SubTask> getSubTasksByEpic(int id) {
        return epics.get(id).getSubTasks();
    }

    /**
     * Returns a task by id
     * @param id
     * @return task or null if there was no one
     */
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    /**
     * Returns an epic by id
     * @param id
     * @return epic or null if there was no one
     */
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    /**
     * Returns a subtask by id
     * @param id
     * @return subtask or null if there was no one
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
            epic.clearSubTasks();
        }
        subTasks.clear();
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
        for (SubTask subTask : getEpicById(id).getSubTasks()) {
            subTasks.remove(subTask.getId());
        }
        epics.remove(id);
    }

    /**
     * Deletes a subtask by id
     * @param id
     */
    public void deleteSubTaskById(int id) {
        SubTask task = getSubTaskById(id);
        epics.get(task.getEpic().getId()).removeSubTask(task);
        subTasks.remove(id);
    }

    /**
     * Creates a new task
     * @param task
     */
    public void createTask(Task task) {
        task.setId(++nextTaskId);
        tasks.put(task.getId(), task);
    }

    /**
     * Creates a new epic
     * @param epic
     */
    public void createEpic(Epic epic) {
        epic.setId(++nextTaskId);
        epics.put(epic.getId(), epic);
    }

    /**
     * Creates a new subtask
     * @param subTask
     */
    public void createSubTask(SubTask subTask) {
        subTask.setId(++nextTaskId);
        subTask.getEpic().addSubTask(subTask);
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
        originalTask.getEpic().removeSubTask(originalTask);
        subTask.getEpic().addSubTask(subTask);
        subTasks.put(subTask.getId(), subTask);
    }
}