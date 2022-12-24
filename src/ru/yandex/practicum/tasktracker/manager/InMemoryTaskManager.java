package ru.yandex.practicum.tasktracker.manager;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected int nextTaskId = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();

    private final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparingInt(Task::getId));

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
        epics.values().forEach(Epic::clearSubTasks);
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
        epics.get(id).getSubTasks().stream()
                .map(SubTask::getId)
                .peek(historyManager::remove)
                .forEach(subTasks::remove);
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
        addTaskToPrioritizedTasks(task);
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
        addTaskToPrioritizedTasks(subTask);
        epics.get(subTask.getEpic().getId()).addSubTask(subTask);
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
        int epicId = subTask.getEpic().getId();
        epics.get(epicId).removeSubTask(subTask);
        epics.get(epicId).addSubTask(subTask);
        subTasks.put(subTask.getId(), subTask);
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private void addTaskToPrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);

        if (task.getStartTime() != null) {
            for (Task prioritizedTask : prioritizedTasks) {
                if (prioritizedTask.getStartTime() != null) {
                    checkTasksOverlapInTime(task, prioritizedTask);
                }
            }
        }

        prioritizedTasks.add(task);
    }

    private void checkTasksOverlapInTime(Task firstTask, Task secondTask) {
        if (firstTask.getStartTime().equals(secondTask.getStartTime())
                && firstTask.getEndTime().equals(secondTask.getEndTime())
                || firstTask.getStartTime().isAfter(secondTask.getStartTime())
                && firstTask.getStartTime().isBefore(secondTask.getEndTime())
                || firstTask.getEndTime().isAfter(secondTask.getStartTime())
                && firstTask.getEndTime().isBefore(secondTask.getEndTime())
                || firstTask.getStartTime().isBefore(secondTask.getStartTime())
                && firstTask.getEndTime().isAfter(secondTask.getEndTime())
        ) {

            throw new TaskCreateOrUpdateException("Task execution time overlaps with other tasks");
        }
    }
}