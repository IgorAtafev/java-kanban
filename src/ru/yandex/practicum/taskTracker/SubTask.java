package ru.yandex.practicum.taskTracker;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(int epicId, String name, String description) {
        super(name, description);
        this.epicId = epicId;
    }

    public SubTask(int epicId, String name, String description, int id) {
        super(name, description, id);
        this.epicId = epicId;
    }

    public SubTask(int epicId, String name, String description, int id, Status status) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus().getName() + '\'' +
                '}';
    }
}