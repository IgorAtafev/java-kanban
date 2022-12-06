package ru.yandex.practicum.tasktracker.manager;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Status;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path filename = Path.of("resources/tasks.csv");

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(filename, StandardCharsets.UTF_8)) {
            String title = "id,type,name,status,description,epic";
            writer.write(title);
            writer.newLine();

            for (Task task : getTasks()) {
                writer.write(task.toString());
                writer.newLine();
            }

            for (Epic task : getEpics()) {
                writer.write(task.toString());
                writer.newLine();
            }

            for (SubTask task : getSubTasks()) {
                writer.write(task.toString());
                writer.newLine();
            }

            writer.newLine();
            writer.write(historyToString());
            writer.newLine();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл", e);
        }
    }

    private String historyToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : getHistory()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(task.getId());
        }

        return stringBuilder.toString();
    }
}