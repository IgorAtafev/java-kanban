package ru.yandex.practicum.tasktracker.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Epic extends Task {
    private final Set<SubTask> subTasks = new HashSet<>();

    public List<SubTask> getSubTasks() {
        return List.copyOf(subTasks);
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

        if (isStatusNew) {
            return Status.NEW;
        }

        if (isStatusDone) {
            return Status.DONE;
        }

        return Status.IN_PROGRESS;
    }

    @Override
    public void setStatus(Status status) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object object) {
        if (!super.equals(object)) {
            return false;
        }

        if (!(object instanceof Epic)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
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