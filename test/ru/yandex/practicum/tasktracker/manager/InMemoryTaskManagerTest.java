package ru.yandex.practicum.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.manager.exception.TaskIntersectionException;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Status;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest {
    protected Task task1;
    protected Task task2;
    protected Epic epic1;
    protected Epic epic2;
    protected SubTask subTask1;
    protected SubTask subTask2;
    protected SubTask subTask3;

    private InMemoryTaskManager taskManager;

    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @BeforeEach
    void setUp() throws IOException {
        taskManager = createTaskManager();
        initTasks();
    }

    @Test
    void getHistory_shouldReturnEmptyHistory() {
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void getTasks_shouldCheckForNull() {
        assertNotNull(taskManager.getTasks());
    }

    @Test
    void getTasks_shouldReturnEmptyListOfTasks() {
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void getTasks_shouldReturnListOfTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> expected = List.of(task1, task2);
        List<Task> actual = taskManager.getTasks();

        assertEquals(expected, actual);
    }

    @Test
    void getEpics_shouldCheckForNull() {
        assertNotNull(taskManager.getEpics());
    }

    @Test
    void getEpics_shouldReturnEmptyListOfEpics() {
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    void getEpics_shouldReturnListOfEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        List<Epic> expected = List.of(epic1, epic2);
        List<Epic> actual = taskManager.getEpics();

        assertEquals(expected, actual);
    }

    @Test
    void getSubTasks_shouldCheckForNull() {
        assertNotNull(taskManager.getSubTasks());
    }

    @Test
    void getSubTasks_shouldReturnEmptyListOfSubtasks() {
        assertTrue(taskManager.getSubTasks().isEmpty());
    }

    @Test
    void getSubTasks_shouldReturnListOfSubtasks() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        List<SubTask> expected = List.of(subTask1, subTask2, subTask3);
        List<SubTask> actual = taskManager.getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void getSubTasksByEpic_shouldCheckForNull() {
        taskManager.createEpic(epic1);
        assertNotNull(taskManager.getSubTasksByEpic(epic1.getId()));
    }

    @Test
    void getSubTasksByEpic_shouldReturnListOfSubtasksByEpic() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        List<SubTask> expected = List.of(subTask1, subTask2);
        List<SubTask> actual = taskManager.getSubTasksByEpic(epic1.getId());

        assertEquals(expected, actual);
    }

    @Test
    void getTaskById_shouldReturnTaskById() {
        taskManager.createTask(task1);
        Task task = taskManager.getTaskById(task1.getId());
        assertEquals(task1, task);
    }

    @Test
    void getTaskById_shouldAddTaskToHistory() {
        taskManager.createTask(task1);

        taskManager.getTaskById(task1.getId());

        List<Task> expected = List.of(task1);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void getEpicById_shouldReturnEpicById() {
        taskManager.createEpic(epic1);
        Epic epic = taskManager.getEpicById(epic1.getId());
        assertEquals(epic1, epic);
    }

    @Test
    void getEpicById_shouldAddEpicToHistory() {
        taskManager.createEpic(epic1);

        taskManager.getEpicById(epic1.getId());

        List<Task> expected = List.of(epic1);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void getSubTaskById_shouldReturnSubtaskById() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);

        SubTask subTask = taskManager.getSubTaskById(subTask1.getId());

        assertEquals(subTask1, subTask);
    }

    @Test
    void getSubTaskById_shouldAddSubtaskToHistory() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);

        taskManager.getSubTaskById(subTask1.getId());

        List<Task> expected = List.of(subTask1);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteTaskById_shouldRemoveTask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.deleteTaskById(task1.getId());

        List<Task> expected = List.of(task2);
        List<Task> actual = taskManager.getTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteTaskById_shouldRemoveTaskFromHistory() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        taskManager.historyManager.add(task1);
        taskManager.historyManager.add(task2);
        taskManager.historyManager.add(epic1);
        taskManager.historyManager.add(subTask1);
        taskManager.historyManager.add(subTask2);

        taskManager.deleteTaskById(task1.getId());

        List<Task> expected = List.of(task2, epic1, subTask1, subTask2);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteTaskById_shouldRemoveTaskFromPrioritizedTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.deleteTaskById(task2.getId());

        List<Task> expected = List.of(task1, subTask1, subTask2, subTask3);
        List<Task> actual = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpicById_shouldRemoveEpic() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        taskManager.deleteEpicById(epic1.getId());

        List<Epic> expected = List.of(epic2);
        List<Epic> actual = taskManager.getEpics();

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpicById_shouldRemoveAllEpicSubtasks() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.deleteEpicById(epic1.getId());

        List<SubTask> expected = List.of(subTask3);
        List<SubTask> actual = taskManager.getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpicById_shouldRemoveEpicAndAllEpicSubtasksFromHistory() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.historyManager.add(epic1);
        taskManager.historyManager.add(subTask1);
        taskManager.historyManager.add(subTask2);
        taskManager.historyManager.add(subTask3);

        taskManager.deleteEpicById(epic1.getId());

        List<Task> expected = List.of(subTask3);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpicById_shouldRemoveEpicAndAllEpicSubtasksFromPrioritizedTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.deleteEpicById(epic1.getId());

        List<Task> expected = List.of(task1, task2, subTask3);
        List<Task> actual = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTaskById_shouldRemoveSubtask() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.deleteSubTaskById(subTask1.getId());

        List<SubTask> expected = List.of(subTask2, subTask3);
        List<SubTask> actual = taskManager.getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTaskById_shouldRemoveSubtaskFromEpic() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        taskManager.deleteSubTaskById(subTask1.getId());

        List<SubTask> expected = List.of(subTask2);
        List<SubTask> actual = taskManager.getEpicById(subTask2.getEpic().getId()).getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTaskById_shouldRemoveSubtaskFromHistory() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        taskManager.historyManager.add(subTask1);
        taskManager.historyManager.add(subTask2);

        taskManager.deleteSubTaskById(subTask1.getId());

        List<Task> expected = List.of(subTask2);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTaskById_shouldRemoveSubtaskFromPrioritizedTasks() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.deleteSubTaskById(subTask1.getId());

        List<Task> expected = List.of(subTask2, subTask3);
        List<Task> actual = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expected, actual);
    }

    @Test
    void deleteTasks_shouldRemoveAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.deleteTasks();

        List<Task> expected = List.of();
        List<Task> actual = taskManager.getTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteTasks_shouldRemoveAllTasksFromHistory() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        taskManager.historyManager.add(task1);
        taskManager.historyManager.add(epic1);
        taskManager.historyManager.add(subTask1);
        taskManager.historyManager.add(subTask2);
        taskManager.historyManager.add(task2);

        taskManager.deleteTasks();

        List<Task> expected = List.of(epic1, subTask1, subTask2);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteTasks_shouldRemoveAllTasksFromPrioritizedTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.deleteTasks();

        List<Task> expected = List.of(subTask1, subTask2, subTask3);
        List<Task> actual = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpics_shouldRemoveAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        taskManager.deleteEpics();

        List<Epic> expected = List.of();
        List<Epic> actual = taskManager.getEpics();

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpics_shouldRemoveAllSubtasks() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.deleteEpics();

        List<SubTask> expected = List.of();
        List<SubTask> actual = taskManager.getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpics_shouldRemoveAllEpicsAndAllSubtasksFromHistory() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.historyManager.add(task1);
        taskManager.historyManager.add(task2);
        taskManager.historyManager.add(epic1);
        taskManager.historyManager.add(epic2);
        taskManager.historyManager.add(subTask1);
        taskManager.historyManager.add(subTask2);

        taskManager.deleteEpics();

        List<Task> expected = List.of(task1, task2);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpics_shouldRemoveAllEpicsAndAllSubtasksFromPrioritizedTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.deleteEpics();

        List<Task> expected = List.of(task1, task2);
        List<Task> actual = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTasks_shouldRemoveAllSubtasks() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.deleteSubTasks();

        List<SubTask> expected = List.of();
        List<SubTask> actual = taskManager.getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTasks_shouldRemoveAllEpicSubtasks() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.deleteSubTasks();

        List<Epic> expected = List.of();
        List<Epic> actual = taskManager.getEpics().stream()
                .filter(epic -> !epic.getSubTasks().isEmpty())
                .collect(Collectors.toList());

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTasks_shouldRemoveAllSubtasksFromHistory() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.historyManager.add(task1);
        taskManager.historyManager.add(task2);
        taskManager.historyManager.add(epic1);
        taskManager.historyManager.add(epic2);
        taskManager.historyManager.add(subTask1);
        taskManager.historyManager.add(subTask2);
        taskManager.deleteSubTasks();

        List<Task> expected = List.of(task1, task2, epic1, epic2);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTasks_shouldRemoveAllSubtasksFromPrioritizedTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.deleteSubTasks();

        List<Task> expected = List.of(task1, task2);
        List<Task> actual = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expected, actual);
    }

    @Test
    void getPrioritizedTasks_shouldCheckForNull() {
        assertNotNull(taskManager.getPrioritizedTasks());
    }

    @Test
    void getPrioritizedTasks_shouldReturnListOfPrioritizedTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        List<Task> expected = List.of(task1, task2, subTask1, subTask2, subTask3);
        List<Task> actual = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expected, actual);
    }

    @Test
    void createTask_shouldCreateATask() {
        Task task3 = createTask("Новая задача", "Описание задачи");

        taskManager.createTask(task3);

        List<Task> expected = List.of(task3);
        List<Task> actual = taskManager.getTasks();

        assertEquals(expected, actual);
    }

    @Test
    void createEpic_shouldCreateAnEpic() {
        Epic epic3 = createEpic("Новый эпик", "Описание эпика");

        taskManager.createEpic(epic3);

        List<Epic> expected = List.of(epic3);
        List<Epic> actual = taskManager.getEpics();

        assertEquals(expected, actual);
    }

    @Test
    void createSubTask_shouldCreateASubtask() {
        SubTask subTask4 = createSubTask("Новая подзадача", "Описание подзадачи", epic1);

        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask4);

        List<SubTask> expected = List.of(subTask4);
        List<SubTask> actual = taskManager.getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void updateTask_shouldUpdateTheTask() {
        taskManager.createTask(task1);
        task1.setName("Обновленная задача");
        task1.setDescription("Описание обновленной задачи");

        taskManager.updateTask(task1);

        List<Task> expected = List.of(task1);
        List<Task> actual = taskManager.getTasks();

        assertEquals(expected, actual);

        assertEquals("Обновленная задача", taskManager.getTaskById(task1.getId()).getName());
        assertEquals("Описание обновленной задачи", taskManager.getTaskById(task1.getId()).getDescription());
    }

    @Test
    void updateEpic_shouldUpdateTheEpic() {
        taskManager.createEpic(epic1);
        epic1.setName("Обновленный эпик");
        epic1.setDescription("Описание обновленного эпика");

        taskManager.updateEpic(epic1);

        List<Epic> expected = List.of(epic1);
        List<Epic> actual = taskManager.getEpics();

        assertEquals(expected, actual);

        assertEquals("Обновленный эпик", taskManager.getEpicById(epic1.getId()).getName());
        assertEquals("Описание обновленного эпика", taskManager.getEpicById(epic1.getId()).getDescription());
    }

    @Test
    void updateSubTask_shouldUpdateSubtask() {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        subTask1.setName("Обновленная подзадача");
        subTask1.setDescription("Описание обновленной подзадачи");

        taskManager.updateSubTask(subTask1);

        List<SubTask> expected = List.of(subTask1);
        List<SubTask> actual = taskManager.getSubTasks();

        assertEquals(expected, actual);

        assertEquals("Обновленная подзадача", taskManager.getSubTaskById(subTask1.getId()).getName());
        assertEquals("Описание обновленной подзадачи",
                taskManager.getSubTaskById(subTask1.getId()).getDescription());
    }

    @Test
    void createTask_shouldAddTheTaskToThePrioritizedTasks_ifTasksDoesNotIntersectInTime() {
        taskManager.createTask(task1);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        Task task2 = createTask("Новая задача", "Описание задачи");
        task2.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30));
        task2.setDuration(Duration.ofMinutes(15));
        taskManager.createTask(task2);

        Task task3 = createTask("Новая задача2", "Описание задачи");
        task3.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        task3.setDuration(Duration.ofMinutes(30));
        taskManager.createTask(task3);

        SubTask subTask3 = createSubTask("Новая подзадача", "Описание подзадачи", epic1);
        subTask3.setStartTime(LocalDateTime.of(2022, 12, 22, 10, 0));
        subTask3.setDuration(Duration.ofMinutes(45));
        taskManager.createSubTask(subTask3);

        List<Task> expectedTasks = List.of(task1, task2, task3);
        List<Task> actualTasks = taskManager.getTasks();

        assertEquals(expectedTasks, actualTasks);

        List<SubTask> expectedSubTasks = List.of(subTask1, subTask2, subTask3);
        List<SubTask> actualSubTasks = taskManager.getSubTasks();

        assertEquals(expectedSubTasks, actualSubTasks);

        List<Task> expectedPrioritizedTasks = List.of(subTask3, task3, task2, task1, subTask1, subTask2);
        List<Task> actualPrioritizedTasks = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expectedPrioritizedTasks, actualPrioritizedTasks);
    }

    @Test
    void createTask_shouldThrowAnException_ifTasksIntersectInTime() {
        taskManager.createTask(task1);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        Task task2 = createTask("Новая задача", "Описание задачи");
        task2.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30));
        task2.setDuration(Duration.ofMinutes(15));
        taskManager.createTask(task2);

        SubTask subTask3 = createSubTask("Новая подзадача", "Описание подзадачи", epic1);
        subTask3.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        subTask3.setDuration(Duration.ofMinutes(15));
        taskManager.createSubTask(subTask3);

        SubTask subTask4 = createSubTask("Новая подзадача2", "Описание подзадачи", epic2);
        subTask4.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 40));
        subTask4.setDuration(Duration.ofMinutes(30));

        TaskIntersectionException exception = assertThrows(
                TaskIntersectionException.class,
                () -> taskManager.createSubTask(subTask4)
        );
        assertEquals("Task execution time intersect with other tasks", exception.getMessage());

        Task task3 = createTask("Новая задача", "Описание задачи");
        task3.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 20));
        task3.setDuration(Duration.ofMinutes(20));

        exception = assertThrows(
                TaskIntersectionException.class,
                () -> taskManager.createTask(task3)
        );
        assertEquals("Task execution time intersect with other tasks", exception.getMessage());

        Task task4 = createTask("Новая задача", "Описание задачи");
        task4.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 20));
        task4.setDuration(Duration.ofMinutes(30));

        exception = assertThrows(
                TaskIntersectionException.class,
                () -> taskManager.createTask(task4)
        );
        assertEquals("Task execution time intersect with other tasks", exception.getMessage());

        SubTask subTask5 = createSubTask("Новая подзадача", "Описание подзадачи", epic1);
        subTask5.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30));
        subTask5.setDuration(Duration.ofMinutes(15));

        exception = assertThrows(
                TaskIntersectionException.class,
                () -> taskManager.createSubTask(subTask5)
        );
        assertEquals("Task execution time intersect with other tasks", exception.getMessage());
    }

    @Test
    void updateTask_shouldAddTheTaskToThePrioritizedTasks_ifTasksDoesNotIntersectInTime() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask3);

        task1.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        task1.setDuration(Duration.ofMinutes(30));
        taskManager.updateTask(task1);

        task2.setStartTime(LocalDateTime.of(2022, 12, 22, 10, 0));
        task2.setDuration(Duration.ofMinutes(45));
        taskManager.updateTask(task2);

        subTask1.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30));
        subTask1.setDuration(Duration.ofMinutes(15));
        taskManager.updateSubTask(subTask1);

        subTask2.setStartTime(LocalDateTime.of(2022, 12, 22, 12, 30));
        subTask2.setDuration(Duration.ofMinutes(15));
        taskManager.updateSubTask(subTask2);

        List<Task> expectedTasks = List.of(task1, task2);
        List<Task> actualTasks = taskManager.getTasks();

        assertEquals(expectedTasks, actualTasks);

        List<SubTask> expectedSubTasks = List.of(subTask1, subTask2, subTask3);
        List<SubTask> actualSubTasks = taskManager.getSubTasks();

        assertEquals(expectedSubTasks, actualSubTasks);

        List<Task> expectedPrioritizedTasks = List.of(task2, task1, subTask1, subTask2, subTask3);
        List<Task> actualPrioritizedTasks = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expectedPrioritizedTasks, actualPrioritizedTasks);
    }

    @Test
    void updateTask_shouldThrowAnException_ifTasksIntersectInTime() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask3);

        task1.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30));
        task1.setDuration(Duration.ofMinutes(15));
        taskManager.updateTask(task1);

        subTask2.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        subTask2.setDuration(Duration.ofMinutes(15));
        taskManager.updateSubTask(subTask2);

        task2.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 40));
        task2.setDuration(Duration.ofMinutes(30));

        TaskIntersectionException exception = assertThrows(
                TaskIntersectionException.class,
                () -> taskManager.updateTask(task2)
        );
        assertEquals("Task execution time intersect with other tasks", exception.getMessage());

        subTask1.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 20));
        subTask1.setDuration(Duration.ofMinutes(20));

        exception = assertThrows(
                TaskIntersectionException.class,
                () -> taskManager.updateSubTask(subTask1)
        );
        assertEquals("Task execution time intersect with other tasks", exception.getMessage());

        subTask3.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 20));
        subTask3.setDuration(Duration.ofMinutes(30));

        exception = assertThrows(
                TaskIntersectionException.class,
                () -> taskManager.updateSubTask(subTask3)
        );
        assertEquals("Task execution time intersect with other tasks", exception.getMessage());

        task1.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        task1.setDuration(Duration.ofMinutes(15));

        exception = assertThrows(
                TaskIntersectionException.class,
                () -> taskManager.updateTask(task1)
        );
        assertEquals("Task execution time intersect with other tasks", exception.getMessage());
    }

    protected void initTasks() {
        task1 = createTask("Задача1", "Описание задачи");
        task2 = createTask("Задача2", "Описание задачи");
        epic1 = createEpic("Эпик1", "Описание эпика");
        epic2 = createEpic("Эпик2", "Описание эпика");
        subTask1 = createSubTask("Подзадача1", "Описание подзадачи", epic1);
        subTask2 = createSubTask("Подзадача2", "Описание подзадачи", epic1);
        subTask3 = createSubTask("Подзадача3", "Описание подзадачи", epic2);
    }

    protected Task createTask(String name, String description) {
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setStatus(Status.NEW);
        return task;
    }

    protected Epic createEpic(String name, String description) {
        Epic epic = new Epic();
        epic.setName(name);
        epic.setDescription(description);
        return epic;
    }

    protected SubTask createSubTask(String name, String description, Epic epic) {
        SubTask subTask = new SubTask();
        subTask.setName(name);
        subTask.setDescription(description);
        subTask.setStatus(Status.NEW);
        subTask.setEpic(epic);
        return subTask;
    }
}