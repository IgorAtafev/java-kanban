package ru.yandex.practicum.tasktracker.model;

import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Task)) {
            return false;
        }

        Task task = (Task) object;

        return id == task.id && Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        return id +
                "," + TaskType.TASK +
                "," + name +
                "," + status +
                "," + description +
                ",";
    }

    /**
     * Returns a task from a string
     * @param value
     * @return task
     */
    public Task fromString(String value) {
        //String[] split
        return null;
    }
}