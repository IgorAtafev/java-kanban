package ru.yandex.practicum.tasktracker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> subTasks = new ArrayList<>();

    public List<SubTask> getSubTasks() {
        return Collections.unmodifiableList(subTasks);
    }

    /**
     * Clears the list of subtasks
     */
    public void clearSubTasks() {
        subTasks.clear();
    }

    /**
     * Adds a subtask to the list of subtasks
     * @param subTask
     */
    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
    }

    /**
     * Removes a subtask from the list of subtasks
     * @param subTask
     */
    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask);
    }

    /**
     * Returns epic status
     * If the epic has no subtasks or all of them have the NEW status, then the status should be NEW.
     * If all subtasks have the DONE status, then the epic is considered completed - with the DONE status.
     * In all other cases the status should be IN_PROGRESS.
     * @return epic status
     */
    @Override
    public Status getStatus() {
        if (subTasks.isEmpty()) {
            return Status.NEW;
        }

        boolean isStatusNew = true;
        boolean isStatusDone = true;
        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() != Status.NEW) {
                isStatusNew = false;
            }
            if (subTask.getStatus() != Status.DONE) {
                isStatusDone = false;
            }

            if (!isStatusNew && !isStatusDone) {
                break;
            }
        }

        return isStatusNew ? Status.NEW : isStatusDone ? Status.DONE : Status.IN_PROGRESS;
    }

    @Override
    public void setStatus(Status status) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", subTasks=" + subTasks +
                '}';
    }
}