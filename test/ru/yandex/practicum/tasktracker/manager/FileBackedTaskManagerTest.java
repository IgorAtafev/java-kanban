package ru.yandex.practicum.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        task1.setDuration(30);
        taskManager.updateTask(task1);

        subTask2.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30));
        subTask2.setDuration(15);
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

/*    @Test
    void loadFromFile_shouldLoadTasksFromFileAndRestoreTaskListsAndHistory() {

    }*/
}