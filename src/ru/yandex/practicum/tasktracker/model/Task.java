package ru.yandex.practicum.tasktracker.model;

import ru.yandex.practicum.tasktracker.util.DateTimeFormatterHelper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    private int id;
    private String name;
    private String description;
    private Status status;
    private LocalDateTime startTime;
    private Duration duration;

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
        return TaskType.TASK;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    /**
     * Returns the date and time the task ended, calculated from startTime and duration
     * @return date and time the task ended
     */
    public LocalDateTime getEndTime() {
        if (getStartTime() == null) {
            return null;
        }

        if (getDuration() == null) {
            return getStartTime();
        }

        return getStartTime().plus(getDuration());
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

        return id == task.id  && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && getStatus() == task.getStatus()
                && Objects.equals(getStartTime(), task.getStartTime())
                && Objects.equals(getDuration(), task.getDuration());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, getStatus(), getStartTime(), getDuration());
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration='" + duration + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                '}';
    }

    /**
     * Returns the task as a string in CSV format
     * @return string in CSV format
     */
    public String toCsvRow() {
        String pattern = "dd.MM.yyyy HH:mm";
        String startTime = DateTimeFormatterHelper.format(getStartTime(), pattern);
        String endTime = DateTimeFormatterHelper.format(getEndTime(), pattern);

        long minutes = 0;
        Duration duration = getDuration();
        if (duration != null) {
            minutes = duration.toMinutes();
        }

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s", getId(), getType(), getName(), getStatus(), getDescription(),
                startTime, minutes, endTime);
    }
}
