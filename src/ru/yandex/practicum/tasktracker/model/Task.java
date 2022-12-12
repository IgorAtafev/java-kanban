package ru.yandex.practicum.tasktracker.model;

import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;
    private final TaskType type = TaskType.TASK;

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

    public TaskType getType() {
        return type;
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

        return id == task.id && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    /**
     * Returns the task as a string in CSV format
     * @return string in CSV format
     */
    public String toCsvRow() {
        return new StringBuilder()
                .append(getId())
                .append(",")
                .append(getType())
                .append(",")
                .append(getName())
                .append(",")
                .append(getStatus())
                .append(",")
                .append(getDescription())
                .append(",")
                .toString();
    }
}