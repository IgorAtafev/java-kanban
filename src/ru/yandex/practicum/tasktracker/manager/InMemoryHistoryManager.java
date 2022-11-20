package ru.yandex.practicum.tasktracker.manager;

import ru.yandex.practicum.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        if (history.size() == HISTORY_SIZE) {
            remove(0);
        }
        history.add(task);
    }

    @Override
    public void remove(int id) {
        history.remove(0);
    }
}