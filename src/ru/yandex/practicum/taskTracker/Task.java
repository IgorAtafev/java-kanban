package ru.yandex.practicum.taskTracker;

public class Task {
    private String name;
    private String description;
    private final int id;
    private Status status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = 0;
        this.status = Status.NEW;
    }

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = Status.NEW;
    }

    public Task(String name, String description, int id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
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

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
/*

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || !(object instanceof Task)) {
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
*/

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status.getName() +
                '}';
    }
}