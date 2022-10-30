package ru.yandex.practicum.tasktracker.manager;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final List<Task> taskHistory = new ArrayList<>();

    private final int historySize = 10;
    private int nextTaskId = 0;

    /**
     * Returns a list of all tasks
     * @return list of all tasks
     */
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Returns a list of all epics
     * @return list of all epics
     */
    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Returns a list of all subtasks
     * @return list of all subtasks
     */
    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    /**
     * Returns a list of all subtasks by id epic
     * @param id
     * @return list of all subtasks by id epic
     */
    @Override
    public List<SubTask> getSubTasksByEpic(int id) {
        return epics.get(id).getSubTasks();
    }

    /**
     * Returns a task by id and adds a task to the history
     * @param id
     * @return task or null if there was no one
     */
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        addTaskToHistory(task);
        return task;
    }

    /**
     * Returns an epic by id and adds a epic to the history
     * @param id
     * @return epic or null if there was no one
     */
    @Override
    public Epic getEpicById(int id) {
        Epic task = epics.get(id);
        addTaskToHistory(task);
        return task;
    }

    /**
     * Returns a subtask by id and adds a subtask to the history
     * @param id
     * @return subtask or null if there was no one
     */
    @Override
    public SubTask getSubTaskById(int id) {
        SubTask task = subTasks.get(id);
        addTaskToHistory(task);
        return task;
    }

    /**
     * Deletes all tasks
     */
    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    /**
     * Deletes all epics
     */
    @Override
    public void deleteEpics() {
        subTasks.clear();
        epics.clear();
    }

    /**
     * Deletes all subtasks
     */
    @Override
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
    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    /**
     * Deletes an epic by id
     * @param id
     */
    @Override
    public void deleteEpicById(int id) {
        for (SubTask subTask : epics.get(id).getSubTasks()) {
            subTasks.remove(subTask.getId());
        }
        epics.remove(id);
    }

    /**
     * Deletes a subtask by id
     * @param id
     */
    @Override
    public void deleteSubTaskById(int id) {
        SubTask task = subTasks.get(id);
        epics.get(task.getEpic().getId()).removeSubTask(task);
        subTasks.remove(id);
    }

    /**
     * Creates a new task
     * @param task
     */
    @Override
    public void createTask(Task task) {
        task.setId(++nextTaskId);
        tasks.put(task.getId(), task);
    }

    /**
     * Creates a new epic
     * @param epic
     */
    @Override
    public void createEpic(Epic epic) {
        epic.setId(++nextTaskId);
        epics.put(epic.getId(), epic);
    }

    /**
     * Creates a new subtask
     * @param subTask
     */
    @Override
    public void createSubTask(SubTask subTask) {
        subTask.setId(++nextTaskId);
        subTask.getEpic().addSubTask(subTask);
        subTasks.put(subTask.getId(), subTask);
    }

    /**
     * Updates the task
     * @param task
     */
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    /**
     * Updates the epic
     * @param epic
     */
    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    /**
     * Updates the subtask
     * @param subTask
     */
    @Override
    public void updateSubTask(SubTask subTask) {
        SubTask originalTask = subTasks.get(subTask.getId());
        originalTask.getEpic().removeSubTask(originalTask);
        subTask.getEpic().addSubTask(subTask);
        subTasks.put(subTask.getId(), subTask);
    }

    /**
     * Returns the last 10 viewed tasks
     * @return he last 10 viewed tasks
     */
    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }

    /**
     * Adds a task to history
     * @param task
     */
    private void addTaskToHistory(Task task) {
        if (taskHistory.size() == historySize) {
            taskHistory.remove(0);
        }
        taskHistory.add(task);
    }
}