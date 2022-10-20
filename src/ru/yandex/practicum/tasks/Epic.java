package ru.yandex.practicum.tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<SubTask> subTasks = new ArrayList<>();

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

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
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subTasks.size=" + subTasks.size() +
                '}';
    }
}