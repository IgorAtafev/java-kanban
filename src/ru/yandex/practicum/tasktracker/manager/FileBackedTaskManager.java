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
import java.util.HashMap;
import java.util.Map;

/**
 * Writes to a file and browsing history to a file and restores them from a file
 */
public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path = Path.of("resources/tasks.csv");
    private Map<Integer, Task> tasksFromFile = new HashMap<>();

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic task = super.getEpicById(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask task = super.getSubTaskById(id);
        save();
        return task;
    }

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

    /**
     * Restore manager data from a file
     * @return task manager
     */
    public static FileBackedTaskManager loadFromFile() {
        FileBackedTaskManager taskManager = new FileBackedTaskManager();

        try {
            String fileContents = Files.readString(taskManager.path);

            if (fileContents.isEmpty()) {
                return taskManager;
            }

            String[] lines = fileContents.split(System.lineSeparator());
            boolean isReadHistory = false;
            for (int i = 1; i < lines.length; i++) {
                if (lines[i].isBlank()) {
                    isReadHistory = true;
                } else {
                    if (isReadHistory) {
                        taskManager.createHistoryFromCsv(lines[i]);
                    } else {
                        taskManager.createTaskFromCsv(lines[i]);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла", e);
        }

        return taskManager;
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            String title = "id,type,name,status,description,epic";
            writer.write(title);
            writer.newLine();

            for (Task task : getTasks()) {
                writer.write(writeTaskToCsv(task, TaskType.TASK));
                writer.newLine();
            }

            for (Epic epic : getEpics()) {
                writer.write(writeTaskToCsv(epic, TaskType.EPIC));
                writer.newLine();
            }

            for (SubTask subTask : getSubTasks()) {
                writer.write(writeTaskToCsv(subTask, TaskType.SUBTASK));
                writer.newLine();
            }

            writer.newLine();
            writer.write(writeHistoryToCsv());
            writer.newLine();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл", e);
        }
    }

    private String writeTaskToCsv(Task task, TaskType taskType) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(task.getId());
        stringBuilder.append(",");
        stringBuilder.append(taskType);
        stringBuilder.append(",");
        stringBuilder.append(task.getName());
        stringBuilder.append(",");
        stringBuilder.append(task.getStatus());
        stringBuilder.append(",");
        stringBuilder.append(task.getDescription());
        stringBuilder.append(",");

        if (taskType == TaskType.SUBTASK) {
            stringBuilder.append(((SubTask) task).getEpic().getId());
        }

        return stringBuilder.toString();
    }

    private String writeHistoryToCsv() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Task task : getHistory()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(task.getId());
        }

        return stringBuilder.toString();
    }

    private void createTaskFromCsv(String value) {
        String[] split = value.split(",");
        TaskType taskType = TaskType.valueOf(split[1]);

        Task task = new Task();
        if (taskType == TaskType.EPIC) {
            task = new Epic();
        } else if (taskType == TaskType.SUBTASK) {
            task = new SubTask();
        }

        int taskId = Integer.parseInt(split[0]);
        task.setId(taskId);
        task.setName(split[2]);
        if (taskType != TaskType.EPIC) {
            task.setStatus(Status.valueOf(split[3]));
        }
        task.setDescription(split[4]);

        if (task instanceof Epic) {
            super.updateEpic((Epic) task);
        } else if (task instanceof SubTask) {
            Epic epic = super.getEpicById(Integer.parseInt(split[5]));
            ((SubTask) task).setEpic(epic);
            super.updateSubTask((SubTask) task);
        } else {
            super.updateTask(task);
        }

        tasksFromFile.put(taskId, task);
    }

    private void createHistoryFromCsv(String value) {
        String[] split = value.split(",");

        for (String item : split) {
            int taskId = Integer.parseInt(item);
            if (tasksFromFile.containsKey(taskId)) {
                addHistory(tasksFromFile.get(taskId));
            }
        }
    }
}