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

    private int nextTaskId = 0;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<SubTask> getSubTasksByEpic(int id) {
        return epics.get(id).getSubTasks();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic task = epics.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask task = subTasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public void deleteTasks() {
        historyManager.removeAll(tasks.keySet());
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        historyManager.removeAll(subTasks.keySet());
        historyManager.removeAll(epics.keySet());
        subTasks.clear();
        epics.clear();
    }

    @Override
    public void deleteSubTasks() {
        historyManager.removeAll(subTasks.keySet());
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
        }
        subTasks.clear();
    }

    @Override
    public void deleteTaskById(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        historyManager.remove(id);
        for (SubTask subTask : epics.get(id).getSubTasks()) {
            historyManager.remove(subTask.getId());
            subTasks.remove(subTask.getId());
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        historyManager.remove(id);
        SubTask task = subTasks.get(id);
        epics.get(task.getEpic().getId()).removeSubTask(task);
        subTasks.remove(id);
    }

    @Override
    public void createTask(Task task) {
        task.setId(++nextTaskId);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(++nextTaskId);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        subTask.setId(++nextTaskId);
        subTask.getEpic().addSubTask(subTask);
        subTasks.put(subTask.getId(), subTask);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        SubTask originalTask = subTasks.get(subTask.getId());
        originalTask.getEpic().removeSubTask(originalTask);
        subTask.getEpic().addSubTask(subTask);
        subTasks.put(subTask.getId(), subTask);
    }
}