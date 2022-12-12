package ru.yandex.practicum.tasktracker.manager;

import ru.yandex.practicum.tasktracker.model.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Writes to a file and browsing history to a file and restores them from a file
 */
public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;
    private Map<Integer, Task> tasksFromFile = new HashMap<>();
    private static final String FILE_HEADER = "id,type,name,status,description,epic";

    public FileBackedTaskManager(String fileName) {
        this.path = Path.of("resources/" + fileName);
    }

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
    public static FileBackedTaskManager loadFromFile(String fileName) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(fileName);

        try {
            List<String> lines = Files.readAllLines(taskManager.path, StandardCharsets.UTF_8);

            if (lines.isEmpty()) {
                return taskManager;
            }

            boolean isReadHistory = false;
            int counter = 0;
            for (String line: lines) {
                if (counter++ == 0) {
                    continue;
                }

                if (line.isBlank()) {
                    isReadHistory = true;
                    continue;
                }

                if (isReadHistory) {
                    taskManager.restoreHistoryFromCsv(line);
                } else {
                    taskManager.restoreTaskFromCsv(line);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error reading from file", e);
        }

        taskManager.nextTaskId = Collections.max(taskManager.tasksFromFile.keySet());

        return taskManager;
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(FILE_HEADER);
            writer.newLine();

            List<Task> combinedListOfTasks = Stream.of(getTasks(), getEpics(), getSubTasks())
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            for (Task task : combinedListOfTasks) {
                writer.write(task.toCsvRow());
                writer.newLine();
            }

            writer.newLine();
            writer.write(writeHistoryToCsv());
            writer.newLine();
        } catch (IOException e) {
            throw new ManagerSaveException("Error writing to file", e);
        }
    }

    private String writeHistoryToCsv() {
        return getHistory().stream()
                .map(Task::getId)
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    private void restoreTaskFromCsv(String csvLine) {
        String[] values = csvLine.split(",");
        TaskType taskType = TaskType.valueOf(values[1]);

        Task task = new Task();
        if (taskType == TaskType.EPIC) {
            task = new Epic();
        } else if (taskType == TaskType.SUBTASK) {
            task = new SubTask();
        }

        int taskId = Integer.parseInt(values[0]);
        task.setId(taskId);
        task.setName(values[2]);
        if (taskType != TaskType.EPIC) {
            task.setStatus(Status.valueOf(values[3]));
        }
        task.setDescription(values[4]);

        if (task instanceof Epic) {
            super.updateEpic((Epic) task);
        } else if (task instanceof SubTask) {
            int epicId = Integer.parseInt(values[5]);
            Epic epic = super.getEpicById(epicId);
            ((SubTask) task).setEpic(epic);
            historyManager.remove(epicId);
            super.updateSubTask((SubTask) task);
        } else {
            super.updateTask(task);
        }

        tasksFromFile.put(taskId, task);
    }

    private void restoreHistoryFromCsv(String csvLine) {
        String[] values = csvLine.split(",");

        Arrays.stream(values)
                .map(Integer::parseInt)
                .filter(taskId -> tasksFromFile.containsKey(taskId))
                .collect(Collectors.toList())
                .forEach(taskId -> historyManager.add(tasksFromFile.get(taskId)));
    }
}