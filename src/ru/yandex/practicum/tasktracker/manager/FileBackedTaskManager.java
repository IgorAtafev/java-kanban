package ru.yandex.practicum.tasktracker.manager;

import ru.yandex.practicum.tasktracker.model.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path filename = Path.of("resources/tasks.csv");
    private final Map<Integer, Task> tasks = new HashMap<>();

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

    public static FileBackedTaskManager loadFromFile() {
        FileBackedTaskManager taskManager = new FileBackedTaskManager();

        return taskManager;
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(filename, StandardCharsets.UTF_8)) {
            String title = "id,type,name,status,description,epic";
            writer.write(title);
            writer.newLine();

            for (Task task : getTasks()) {
                writer.write(taskToString(task, TaskType.TASK));
                writer.newLine();
            }

            for (Epic epic : getEpics()) {
                writer.write(taskToString(epic, TaskType.EPIC));
                writer.newLine();
            }

            for (SubTask subTask : getSubTasks()) {
                writer.write(taskToString(subTask, TaskType.SUBTASK));
                writer.newLine();
            }

            writer.newLine();
            writer.write(historyToString());
            writer.newLine();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл", e);
        }
    }

    private String taskToString(Task task, TaskType taskType) {
        String string =  task.getId()
                + "," + taskType
                + "," + task.getName()
                + "," + task.getStatus()
                + "," + task.getDescription()
                + ",";

        if (taskType == TaskType.SUBTASK) {
            string += ((SubTask) task).getEpic().getId();
        }

        return string;
    }

    private Task taskFromString(String value) {
        String[] split = value.split(",");

        Task task = new Task();
        TaskType taskType = TaskType.valueOf(split[1]);

        if (taskType == TaskType.SUBTASK) {
            task = new SubTask();
            Epic epic = getEpicById(Integer.parseInt(split[5]));
            ((SubTask) task).setEpic(epic);
            epic.addSubTask((SubTask) task);
        } else if (taskType == TaskType.EPIC) {
            task = new Epic();
        }

        int taskId = Integer.parseInt(split[0]);
        task.setId(taskId);
        task.setName(split[2]);
        if (taskType != TaskType.EPIC) {
            task.setStatus(Status.valueOf(split[3]));
        }
        task.setDescription(split[4]);

        tasks.put(taskId, task);

        return task;
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

    private List<Task> historyFromString(String value) {
        //List<Task> history = new
        return null;
    }
}