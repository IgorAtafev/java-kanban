package ru.yandex.practicum.tasks;

import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {
    private final Map<Integer, SubTask> subTasks = new HashMap<>();

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    /**
     * Gets epic status
     * If the epic has no subtasks or all of them have the NEW status, then the status should be NEW.
     * If all subtasks have the DONE status, then the epic is considered completed - with the DONE status.
     * In all other cases the status should be IN_PROGRESS.
     * @return
     */
    @Override
    public Status getStatus() {
        if (subTasks.isEmpty()) {
            return Status.NEW;
        }

        boolean isStatusNew = true;
        boolean isStatusDone = true;
        for (SubTask subTask : subTasks.values()) {
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
    public String toString() {
        String result = "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus().getName() +
                ", subTasks=";

        if (!subTasks.isEmpty()) {
            int counter = 0;
            for (SubTask subTask : subTasks.values()) {
                if (counter++ > 0) {
                    result += ", ";
                }
                result += subTask.toString();
            }
        } else {
            result += "null";
        }

        result += '}';

        return result;
    }
}