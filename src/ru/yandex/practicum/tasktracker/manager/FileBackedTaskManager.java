package ru.yandex.practicum.tasktracker.manager;

import ru.yandex.practicum.tasktracker.manager.exception.ManagerSaveException;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Status;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.model.TaskType;
import ru.yandex.practicum.tasktracker.util.DateTimeFormatterHelper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Writes to a file and browsing history to a file and restores them from a file
 */
public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String FILE_HEADER = "id,type,name,status,description,start_time,duration,end_time,epic";

    private static final int TASK_ID_INDEX = 0;
    private static final int TASK_TYPE_INDEX = 1;
    private static final int TASK_NAME_INDEX = 2;
    private static final int TASK_STATUS_INDEX = 3;
    private static final int TASK_DESCRIPTION_INDEX = 4;
    private static final int TASK_START_TIME_INDEX = 5;
    private static final int TASK_DURATION_INDEX = 6;
    private static final int TASK_EPIC_INDEX = 8;

    private final Path path;
    private final Map<Integer, Task> tasksFromFile = new HashMap<>();

    private FileBackedTaskManager(String fileName) {
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

            if (lines.size() <= 1) {
                return taskManager;
            }

            boolean isReadHistory = false;
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
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

            taskManager.nextTaskId = Collections.max(taskManager.tasksFromFile.keySet());
        } catch (IOException e) {
            throw new ManagerSaveException("Error reading from file", e);
        }

        return taskManager;
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(FILE_HEADER);
            writer.newLine();

            String lines = Stream.of(getTasks(), getEpics(), getSubTasks())
                    .flatMap(List::stream)
                    .map(Task::toCsvRow)
                    .collect(Collectors.joining(System.lineSeparator()));

            writer.write(lines);
            writer.newLine();

            if (!getHistory().isEmpty()) {
                writer.newLine();
                writer.write(writeHistoryToCsv());
                writer.newLine();
            }
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
        TaskType taskType = TaskType.valueOf(values[TASK_TYPE_INDEX]);

        Task task = new Task();
        if (taskType == TaskType.EPIC) {
            task = new Epic();
        } else if (taskType == TaskType.SUBTASK) {
            task = new SubTask();
        }

        int taskId = Integer.parseInt(values[TASK_ID_INDEX]);
        task.setId(taskId);
        task.setName(values[TASK_NAME_INDEX]);
        if (taskType != TaskType.EPIC) {
            task.setStatus(Status.valueOf(values[TASK_STATUS_INDEX]));
            task.setStartTime(DateTimeFormatterHelper.parse(values[TASK_START_TIME_INDEX], "dd.MM.yyyy HH:mm"));

            Duration duration = null;
            long minutes = Integer.parseInt(values[TASK_DURATION_INDEX]);
            if (minutes != 0) {
                duration = Duration.ofMinutes(minutes);
            }
            task.setDuration(duration);
        }
        task.setDescription(values[TASK_DESCRIPTION_INDEX]);

        if (task instanceof Epic) {
            super.updateEpic((Epic) task);
        } else if (task instanceof SubTask) {
            int epicId = Integer.parseInt(values[TASK_EPIC_INDEX]);
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
                .filter(tasksFromFile::containsKey)
                .forEach(taskId -> historyManager.add(tasksFromFile.get(taskId)));
    }
}