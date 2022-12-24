package ru.yandex.practicum.tasktracker.model;

import java.util.Objects;

public class SubTask extends Task {
    private Epic epic;

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public boolean equals(Object object) {
        if (!super.equals(object)) {
            return false;
        }

        if (!(object instanceof SubTask)) {
            return false;
        }

        SubTask subTask = (SubTask) object;

        return Objects.equals(epic, subTask.epic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epic);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", startTime='" + getStartTime() + '\'' +
                ", duration='" + getDuration() + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                ", epicId=" + epic.getId() +
                '}';
    }

    @Override
    public String toCsvRow() {
        return String.format("%s,%d", super.toCsvRow(), epic.getId());
    }
}