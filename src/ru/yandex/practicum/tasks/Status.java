package ru.yandex.practicum.tasks;

public enum Status {
    NEW ("Новая"),
    IN_PROGRESS("В работе"),
    DONE("Выполнена");

    private String name;

    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}