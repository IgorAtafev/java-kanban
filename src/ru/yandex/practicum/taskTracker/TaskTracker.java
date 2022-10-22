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
     * Gets a list of all subtasks
     * @return list of subtasks or null
     */
    public List<SubTask> getSubTasks() {
        if (epics.isEmpty()) {
            return null;
        }

        List<SubTask> subTasks = new ArrayList<>();
        for (Epic epic : epics.values()) {
            for (SubTask subTask : epic.getSubTasks().values()) {
                subTasks.add(subTask);
            }
        }

        return subTasks;
    }

    /**
     * Gets a list of all subtasks by epic
     * @param epic
     * @return list of subtasks or null
     */
    public List<SubTask> getSubTasksByEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
           return null;
        }

        return new ArrayList<>(epic.getSubTasks().values());
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
     * @return subtask or null
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
     * Deletes all subtasks
     */
    public void deleteSubTasks() {
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
        }
    }

    /**
     * Deletes all subtasks by epic
     * @param epic
     */
    public void deleteSubTasksByEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            epic.getSubTasks().clear();
        }
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
     * @return subtask or null
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
     * @return task or null
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
     * @return epic or null
     */
    public Epic createEpic(Epic epic) {
        if (epic == null) {
            return null;
        }

        Epic newTask = new Epic(epic.getName(), epic.getDescription(), ++nextEpicId);
        if (epics.containsKey(newTask.getId())) {
            return null;
        }
        epics.put(newTask.getId(), newTask);

        return newTask;
    }

    /**
     * Creates a new subtask
     * @param subTask
     * @return subtask or null
     */
    public SubTask createSubTask(SubTask subTask) {
        if (subTask == null) {
            return null;
        }

        Epic epic = epics.get(subTask.getEpicId());
        if (epic == null) {
            return null;
        }

        SubTask newTask = new SubTask(epic.getId(), subTask.getName(), subTask.getDescription(), ++nextSubTaskId);
        if (epic.getSubTasks().containsKey(newTask.getId())) {
            return null;
        }
        epic.getSubTasks().put(newTask.getId(), newTask);

        return newTask;
    }

    /**
     * Updates the task
     * @param task
     * @return task or null
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
     * @return epic or null
     */
    public Epic updateEpic(Epic epic) {
        if (epic == null) {
            return null;
        }

        Epic originalTask = epics.get(epic.getId());
        if (originalTask == null) {
            return null;
        }

        originalTask.setName(epic.getName());
        originalTask.setDescription(epic.getDescription());

        return originalTask;
    }

    /**
     * Updates the subtask
     * @param subTask
     * @return subtask or null
     */
    public SubTask updateSubTask(SubTask subTask) {
        if (subTask == null) {
            return null;
        }

        Epic epic = epics.get(subTask.getEpicId());
        if (epic == null) {
            return null;
        }

        SubTask originalTask = epic.getSubTasks().get(subTask.getId());
        if (originalTask == null) {
            return null;
        }

        originalTask.setName(subTask.getName());
        originalTask.setDescription(subTask.getDescription());
        originalTask.setStatus(subTask.getStatus());

        return originalTask;
    }
}