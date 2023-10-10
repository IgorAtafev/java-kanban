package ru.yandex.practicum.tasktracker.manager;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {

    /**
     * Returns a list of viewed tasks
     * @return list of viewed tasks
     */
    List<Task> getHistory();

    /**
     * Returns a list of all tasks
     * @return list of all tasks
     */
    List<Task> getTasks();

    /**
     * Returns a list of all epics
     * @return list of all epics
     */
    List<Epic> getEpics();

    /**
     * Returns a list of all subtasks
     * @return list of all subtasks
     */
    List<SubTask> getSubTasks();

    /**
     * Returns a list of all subtasks by id epic
     * @param id
     * @return list of all subtasks by id epic
     */
    List<SubTask> getSubTasksByEpic(int id);

    /**
     * Returns a task by id and adds a task to the history
     * @param id
     * @return task or null if there was no one
     */
    Task getTaskById(int id);

    /**
     * Returns an epic by id and adds an epic to the history
     * @param id
     * @return epic or null if there was no one
     */
    Epic getEpicById(int id);

    /**
     * Returns a subtask by id and adds a subtask to the history
     * @param id
     * @return subtask or null if there was no one
     */
    SubTask getSubTaskById(int id);

    /**
     * Deletes all tasks
     */
    void deleteTasks();

    /**
     * Deletes all epics
     */
    void deleteEpics();

    /**
     * Deletes all subtasks
     */
    void deleteSubTasks();

    /**
     * Deletes a task by id
     * @param id
     */
    void deleteTaskById(int id);

    /**
     * Deletes an epic by id
     * @param id
     */
     void deleteEpicById(int id);

    /**
     * Deletes a subtask by id
     * @param id
     */
    void deleteSubTaskById(int id);

    /**
     * Creates a new task
     * @param task
     */
    void createTask(Task task);

    /**
     * Creates a new epic
     * @param epic
     */
    void createEpic(Epic epic);

    /**
     * Creates a new subtask
     * @param subTask
     */
    void createSubTask(SubTask subTask);

    /**
     * Updates the task
     * @param task
     */
    void updateTask(Task task);

    /**
     * Updates the epic
     * @param epic
     */
    void updateEpic(Epic epic);

    /**
     * Updates the subtask
     * @param subTask
     */
    void updateSubTask(SubTask subTask);

    /**
     * Returns a list of tasks and subtasks sorted by start time
     * @return list of tasks and subtasks
     */
    Set<Task> getPrioritizedTasks();
}
