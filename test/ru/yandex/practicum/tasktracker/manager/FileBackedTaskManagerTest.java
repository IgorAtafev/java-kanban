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
    private static final String EMPTY_FILE = "test/empty.csv";
    private static final String FILE_TO_SAVE = "test/save.csv";
    private static final String FILE_TO_LOAD = "test/load.csv";

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return FileBackedTaskManager.load(EMPTY_FILE);
    }

    @BeforeEach
    @Override
    void setUp() throws IOException {
        Files.writeString(Path.of("resources/" + EMPTY_FILE), "");
        super.setUp();
    }

    @Test
    void save_shouldSaveTasksToAFile() throws IOException {
        Path path = Path.of("resources/" + FILE_TO_SAVE);
        Files.writeString(path, "");
        FileBackedTaskManager taskManager = FileBackedTaskManager.load(FILE_TO_SAVE);

        task1.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        task1.setDuration(Duration.ofMinutes(30));
        taskManager.createTask(task1);

        task2.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 40));
        task2.setDuration(Duration.ofMinutes(15));
        taskManager.createTask(task2);

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        subTask1.setStartTime(LocalDateTime.of(2022, 12, 22, 10, 30));
        subTask1.setDuration(Duration.ofMinutes(20));
        taskManager.createSubTask(subTask1);

        subTask2.setStartTime(LocalDateTime.of(2022, 12, 22, 10, 50));
        subTask2.setDuration(Duration.ofMinutes(10));
        taskManager.createSubTask(subTask2);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());
        taskManager.getEpicById(epic2.getId());

        task1.setStartTime(LocalDateTime.of(2022, 12, 22, 9, 0));
        task1.setDuration(Duration.ofMinutes(40));
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);

        subTask2.setStartTime(LocalDateTime.of(2022, 12, 22, 10, 15));
        subTask2.setDuration(Duration.ofMinutes(15));
        subTask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask2);

        epic1.setName("Обновленный эпик");
        epic1.setDescription("Описание обновленного эпика");
        taskManager.updateEpic(epic1);

        taskManager.deleteTaskById(task1.getId());
        taskManager.createSubTask(subTask3);
        taskManager.getSubTaskById(subTask3.getId());
        taskManager.deleteEpicById(epic1.getId());
        taskManager.deleteSubTaskById(subTask3.getId());
        taskManager.deleteTasks();

        Task task4 = createTask("Задача4", "Описание задачи");
        task4.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        task4.setDuration(Duration.ofMinutes(30));
        task4.setStatus(Status.NEW);
        taskManager.createTask(task4);

        taskManager.deleteEpics();

        Epic epic3 = createEpic("Эпик3", "Описание эпика");
        taskManager.createEpic(epic3);
        Epic epic4 = createEpic("Эпик4", "Описание эпика");
        taskManager.createEpic(epic4);

        SubTask subTask4 = createSubTask("Подзадача4", "Описание подзадачи", epic4);
        subTask4.setStartTime(LocalDateTime.of(2022, 12, 22, 17, 0));
        subTask4.setDuration(Duration.ofMinutes(30));
        subTask4.setStatus(Status.DONE);
        taskManager.createSubTask(subTask4);
        SubTask subTask5 = createSubTask("Подзадача5", "Описание подзадачи", epic4);
        subTask5.setStartTime(LocalDateTime.of(2022, 12, 22, 17, 30));
        subTask5.setDuration(Duration.ofMinutes(30));
        subTask5.setStatus(Status.DONE);
        taskManager.createSubTask(subTask5);

        taskManager.getTaskById(task4.getId());
        taskManager.getEpicById(epic4.getId());
        taskManager.getSubTaskById(subTask5.getId());

        taskManager.deleteSubTasks();

        String expected = "id,type,name,status,description,start_time,duration,end_time,epic"
                + System.lineSeparator()
                + "8,TASK,Задача4,NEW,Описание задачи,22.12.2022 11:00,30,22.12.2022 11:30"
                + System.lineSeparator()
                + "9,EPIC,Эпик3,NEW,Описание эпика,,0,"
                + System.lineSeparator()
                + "10,EPIC,Эпик4,NEW,Описание эпика,,0,"
                + System.lineSeparator()
                + System.lineSeparator()
                +"8,10"
                + System.lineSeparator();;

        String actual = Files.readString(path);

        assertEquals(expected, actual);
    }

    @Test
    void load_shouldThrowAnException_ifTheFileIsNotFound() {
        ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                () -> FileBackedTaskManager.load("file_not_found")
        );
        assertEquals("Error reading from file", exception.getMessage());
    }

    @Test
    void load_shouldLoadTasksFromFileAndRestoreTaskListsAndHistory() {
        FileBackedTaskManager taskManager = FileBackedTaskManager.load(FILE_TO_LOAD);

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
    }
}