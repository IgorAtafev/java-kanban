package ru.yandex.practicum.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.manager.exception.ManagerSaveException;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Status;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {
    private static final String FILE_HEADER = "id,type,name,status,description,start_time,duration,end_time,epic";

    private final String emptyFile = "test/empty.csv";
    private final String fileToSave = "test/save.csv";
    private final String fileToLoad = "test/load.csv";

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return FileBackedTaskManager.loadFromFile(emptyFile);
    }

    @BeforeEach
    void setUp() throws IOException {
        Files.writeString(Path.of("resources/" + emptyFile), "");
        super.setUp();
    }
/*
    @Test
    void createTask_shouldSaveTasksToAFile() throws IOException {
        Path path = Path.of("resources/" + fileToSave);
        Files.writeString(path, "");
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(fileToSave);

        createTestTasks(taskManager);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());

        String expected = String.format("%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%n%d,%d,%d%n", FILE_HEADER,
                task1.toCsvRow(), task2.toCsvRow(), epic1.toCsvRow(), epic2.toCsvRow(),
                subTask1.toCsvRow(), subTask2.toCsvRow(), subTask3.toCsvRow(),
                task1.getId(), epic1.getId(), subTask1.getId());

        String actual = Files.readString(path);

        assertEquals(expected, actual);
    }

    @Test
    void updateTask_shouldSaveTasksToAFile() throws IOException {
        Path path = Path.of("resources/" + fileToSave);
        Files.writeString(path, "");
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(fileToSave);

        createTestTasks(taskManager);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());

        task1.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        task1.setDuration(Duration.ofMinutes(30));
        taskManager.updateTask(task1);

        subTask2.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30));
        subTask2.setDuration(Duration.ofMinutes(15));
        taskManager.updateSubTask(subTask2);

        String expected = String.format("%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%n%d,%d,%d%n", FILE_HEADER,
                task1.toCsvRow(), task2.toCsvRow(), epic1.toCsvRow(), epic2.toCsvRow(),
                subTask1.toCsvRow(), subTask2.toCsvRow(), subTask3.toCsvRow(),
                task1.getId(), epic1.getId(), subTask1.getId());

        String actual = Files.readString(path);

        assertEquals(expected, actual);
    }

    @Test
    void updateEpic_shouldSaveTasksToAFile() throws IOException {
        Path path = Path.of("resources/" + fileToSave);
        Files.writeString(path, "");
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(fileToSave);

        createTestTasks(taskManager);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());

        epic1.setName("Обновленный эпик");
        epic1.setDescription("Описание обновленного эпика");
        taskManager.updateEpic(epic1);

        String expected = String.format("%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%n%d,%d,%d%n", FILE_HEADER,
                task1.toCsvRow(), task2.toCsvRow(), epic1.toCsvRow(), epic2.toCsvRow(),
                subTask1.toCsvRow(), subTask2.toCsvRow(), subTask3.toCsvRow(),
                task1.getId(), epic1.getId(), subTask1.getId());

        String actual = Files.readString(path);

        assertEquals(expected, actual);
    }

    @Test
    void deleteTaskById_shouldSaveTasksToAFile() throws IOException {
        Path path = Path.of("resources/" + fileToSave);
        Files.writeString(path, "");
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(fileToSave);

        createTestTasks(taskManager);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());

        taskManager.deleteTaskById(task1.getId());

        String expected = String.format("%s%n%s%n%s%n%s%n%s%n%s%n%s%n%n%d,%d%n", FILE_HEADER,
                task2.toCsvRow(), epic1.toCsvRow(), epic2.toCsvRow(),
                subTask1.toCsvRow(), subTask2.toCsvRow(), subTask3.toCsvRow(),
                epic1.getId(), subTask1.getId());

        String actual = Files.readString(path);

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpicById_shouldSaveTasksToAFile() throws IOException {
        Path path = Path.of("resources/" + fileToSave);
        Files.writeString(path, "");
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(fileToSave);

        createTestTasks(taskManager);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());

        taskManager.deleteEpicById(epic1.getId());

        String expected = String.format("%s%n%s%n%s%n%s%n%s%n%n%d%n", FILE_HEADER, task1.toCsvRow(),
                task2.toCsvRow(), epic2.toCsvRow(), subTask3.toCsvRow(), task1.getId());

        String actual = Files.readString(path);

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTaskById_shouldSaveTasksToAFile() throws IOException {
        Path path = Path.of("resources/" + fileToSave);
        Files.writeString(path, "");
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(fileToSave);

        createTestTasks(taskManager);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());

        taskManager.deleteSubTaskById(subTask1.getId());

        String expected = String.format("%s%n%s%n%s%n%s%n%s%n%s%n%s%n%n%d,%d%n", FILE_HEADER,
                task1.toCsvRow(), task2.toCsvRow(), epic1.toCsvRow(), epic2.toCsvRow(),
                subTask2.toCsvRow(), subTask3.toCsvRow(),
                task1.getId(), epic1.getId());

        String actual = Files.readString(path);

        assertEquals(expected, actual);
    }

    @Test
    void deleteTasks_shouldSaveTasksToAFile() throws IOException {
        Path path = Path.of("resources/" + fileToSave);
        Files.writeString(path, "");
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(fileToSave);

        createTestTasks(taskManager);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());

        taskManager.deleteTasks();

        String expected = String.format("%s%n%s%n%s%n%s%n%s%n%s%n%n%d,%d%n", FILE_HEADER,
                epic1.toCsvRow(), epic2.toCsvRow(),
                subTask1.toCsvRow(), subTask2.toCsvRow(), subTask3.toCsvRow(),
                epic1.getId(), subTask1.getId());

        String actual = Files.readString(path);

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpics_shouldSaveTasksToAFile() throws IOException {
        Path path = Path.of("resources/" + fileToSave);
        Files.writeString(path, "");
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(fileToSave);

        createTestTasks(taskManager);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());

        taskManager.deleteEpics();

        String expected = String.format("%s%n%s%n%s%n%n%d%n", FILE_HEADER, task1.toCsvRow(),
                task2.toCsvRow(), task1.getId());

        String actual = Files.readString(path);

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTasks_shouldSaveTasksToAFile() throws IOException {
        Path path = Path.of("resources/" + fileToSave);
        Files.writeString(path, "");
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(fileToSave);

        createTestTasks(taskManager);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());

        taskManager.deleteSubTasks();

        String expected = String.format("%s%n%s%n%s%n%s%n%s%n%n%d,%d%n", FILE_HEADER,
                task1.toCsvRow(), task2.toCsvRow(), epic1.toCsvRow(), epic2.toCsvRow(),
                task1.getId(), epic1.getId());

        String actual = Files.readString(path);

        assertEquals(expected, actual);
    }

    @Test
    void loadFromFile_shouldThrowAnException_ifTheFileIsNotFound() {
        ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                () -> FileBackedTaskManager.loadFromFile("file_not_found")
        );
        assertEquals("Error reading from file", exception.getMessage());
    }

    @Test
    void loadFromFile_shouldLoadTasksFromFileAndRestoreTaskListsAndHistory() {
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(fileToLoad);

        Task task1 = createTask("Задача1", "Описание задачи");
        task1.setId(1);
        task1.setStatus(Status.IN_PROGRESS);
        task1.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 15));
        task1.setDuration(Duration.ofMinutes(30));

        Task task2 = createTask("Задача2", "Описание задачи");
        task2.setId(2);
        task2.setStatus(Status.NEW);
        task2.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        task2.setDuration(Duration.ofMinutes(15));

        Epic epic1 = createEpic("Эпик1", "Описание эпика");
        epic1.setId(3);
        Epic epic2 = createEpic("Эпик2", "Описание эпика");
        epic2.setId(4);

        SubTask subTask1 = createSubTask("Подзадача1", "Описание подзадачи", epic1);
        subTask1.setId(5);
        subTask1.setStatus(Status.DONE);
        subTask1.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 50));
        subTask1.setDuration(Duration.ofMinutes(15));
        epic1.addSubTask(subTask1);

        SubTask subTask2 = createSubTask("Подзадача2", "Описание подзадачи", epic1);
        subTask2.setId(6);
        subTask2.setStatus(Status.DONE);
        subTask2.setStartTime(LocalDateTime.of(2022, 12, 22, 12, 5));
        subTask2.setDuration(Duration.ofMinutes(25));
        epic1.addSubTask(subTask2);

        List<Task> expectedTasks = List.of(task1, task2);
        List<Task> actualTasks = taskManager.getTasks();

        assertEquals(expectedTasks, actualTasks);

        List<Epic> expectedEpics = List.of(epic1, epic2);
        List<Epic> actualEpics = taskManager.getEpics();

        assertEquals(expectedEpics, actualEpics);

        List<SubTask> expectedSubTasks = List.of(subTask1, subTask2);
        List<SubTask> actualSubTasks = taskManager.getSubTasks();

        assertEquals(expectedSubTasks, actualSubTasks);

        List<Task> expectedHistory = List.of(epic1, task2, subTask2);
        List<Task> actualHistory = taskManager.getHistory();

        assertEquals(expectedHistory, actualHistory);

        assertTrue(taskManager.nextTaskId == 6);

        List<Task> expectedPrioritizedTasks = List.of(task2, task1, subTask1, subTask2);
        List<Task> actualPrioritizedTasks = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expectedPrioritizedTasks, actualPrioritizedTasks);
    }*/
}