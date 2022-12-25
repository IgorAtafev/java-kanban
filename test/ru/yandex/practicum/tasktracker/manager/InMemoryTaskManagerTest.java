package ru.yandex.practicum.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Status;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest {
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;
    private SubTask subTask3;

    private final InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @BeforeEach
    void setUp() {
        createTestTasks();
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
    void getTasks_shouldReturnListOfTasks() {
        List<Task> expected = List.of(task1, task2);
        List<Task> actual = taskManager.getTasks();

        assertEquals(expected, actual);
    }

    @Test
    void getEpics_shouldCheckForNull() {
        assertNotNull(taskManager.getEpics());
    }

    @Test
    void getEpics_shouldReturnListOfEpics() {
        List<Epic> expected = List.of(epic1, epic2);
        List<Epic> actual = taskManager.getEpics();

        assertEquals(expected, actual);
    }

    @Test
    void getSubTasks_shouldCheckForNull() {
        assertNotNull(taskManager.getSubTasks());
    }

    @Test
    void getSubTasks_shouldReturnListOfSubtasks() {
        List<SubTask> expected = List.of(subTask1, subTask2, subTask3);
        List<SubTask> actual = taskManager.getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void getSubTasksByEpic_shouldCheckForNull() {
        assertNotNull(taskManager.getSubTasksByEpic(epic1.getId()));
    }

    @Test
    void getSubTasksByEpic_shouldReturnListOfSubtasksByEpic() {
        List<SubTask> expected = List.of(subTask1, subTask2);
        List<SubTask> actual = taskManager.getSubTasksByEpic(epic1.getId());

        assertEquals(expected, actual);
    }

    @Test
    void getTaskById_shouldReturnTaskById() {
        Task task = taskManager.getTaskById(task1.getId());
        assertEquals(task1, task);
    }

    @Test
    void getTaskById_shouldAddTaskToHistory() {
        taskManager.getTaskById(task1.getId());

        List<Task> expected = List.of(task1);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void getEpicById_shouldReturnEpicById() {
        Epic epic = taskManager.getEpicById(epic1.getId());
        assertEquals(epic1, epic);
    }

    @Test
    void getEpicById_shouldAddEpicToHistory() {
        taskManager.getEpicById(epic1.getId());

        List<Task> expected = List.of(epic1);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void getSubTaskById_shouldReturnSubtaskById() {
        SubTask subTask = taskManager.getSubTaskById(subTask1.getId());
        assertEquals(subTask1, subTask);
    }

    @Test
    void getSubTaskById_shouldAddSubtaskToHistory() {
        taskManager.getSubTaskById(subTask1.getId());

        List<Task> expected = List.of(subTask1);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteTaskById_shouldRemoveTask() {
        taskManager.deleteTaskById(task1.getId());

        List<Task> expected = List.of(task2);
        List<Task> actual = taskManager.getTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteTaskById_shouldRemoveTaskFromHistory() {
        taskManager.historyManager.add(task1);
        taskManager.deleteTaskById(task1.getId());

        List<Task> expected = List.of();
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpicById_shouldRemoveEpic() {
        taskManager.deleteEpicById(epic1.getId());

        List<Epic> expected = List.of(epic2);
        List<Epic> actual = taskManager.getEpics();

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpicById_shouldRemoveAllEpicSubtasks() {
        taskManager.deleteEpicById(epic1.getId());

        List<SubTask> expected = List.of(subTask3);
        List<SubTask> actual = taskManager.getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpicById_shouldRemoveEpicAndAllEpicSubtasksFromHistory() {
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
    void deleteSubTaskById_shouldRemoveSubtask() {
        taskManager.deleteSubTaskById(subTask1.getId());

        List<SubTask> expected = List.of(subTask2, subTask3);
        List<SubTask> actual = taskManager.getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTaskById_shouldRemoveSubtaskFromEpic() {
        taskManager.deleteSubTaskById(subTask1.getId());

        List<SubTask> expected = List.of(subTask2);
        List<SubTask> actual = taskManager.getEpicById(subTask1.getEpic().getId()).getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTaskById_shouldRemoveSubtaskFromHistory() {
        taskManager.historyManager.add(subTask1);
        taskManager.deleteSubTaskById(subTask1.getId());

        List<Task> expected = List.of();
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteTasks_shouldRemoveAllTasks() {
        taskManager.deleteTasks();

        List<Task> expected = List.of();
        List<Task> actual = taskManager.getTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteTasks_shouldRemoveAllTasksFromHistory() {
        taskManager.historyManager.add(task1);
        taskManager.historyManager.add(task2);
        taskManager.historyManager.add(epic1);
        taskManager.historyManager.add(epic2);
        taskManager.historyManager.add(subTask1);
        taskManager.historyManager.add(subTask2);
        taskManager.deleteTasks();

        List<Task> expected = List.of(epic1, epic2, subTask1, subTask2);
        List<Task> actual = taskManager.getHistory();

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpics_shouldRemoveAllEpics() {
        taskManager.deleteEpics();

        List<Epic> expected = List.of();
        List<Epic> actual = taskManager.getEpics();

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpics_shouldRemoveAllSubtasks() {
        taskManager.deleteEpics();

        List<SubTask> expected = List.of();
        List<SubTask> actual = taskManager.getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteEpics_shouldRemoveAllEpicsAndAllSubtasksFromHistory() {
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
    void deleteSubTasks_shouldRemoveAllSubtasks() {
        taskManager.deleteSubTasks();

        List<SubTask> expected = List.of();
        List<SubTask> actual = taskManager.getSubTasks();

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTasks_shouldRemoveAllEpicSubtasks() {
        taskManager.deleteSubTasks();

        List<Epic> expected = List.of();
        List<Epic> actual = taskManager.getEpics().stream()
                .filter(epic -> !epic.getSubTasks().isEmpty())
                .collect(Collectors.toList());

        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTasks_shouldRemoveAllSubtasksFromHistory() {
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
    void getPrioritizedTasks_shouldCheckForNull() {
        assertNotNull(taskManager.getPrioritizedTasks());
    }

    @Test
    void getPrioritizedTasks_shouldReturnListOfPrioritizedTasks() {
        List<Task> expected = List.of(task1, task2, subTask1, subTask2, subTask3);
        List<Task> actual = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expected, actual);
    }

    @Test
    void createTask_shouldAddTheTaskToThePrioritizedTasks_ifTasksDoesNotIntersectInTime() {
        Task task3 = createTask("Новая задача", "Описание задачи");
        task3.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30));
        task3.setDuration(15);

        Task task4 = createTask("Новая задача2", "Описание задачи");
        task4.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        task4.setDuration(30);

        Task task5 = createTask("Новая задача5", "Описание задачи");
        task5.setStartTime(LocalDateTime.of(2022, 12, 22, 10, 0));
        task5.setDuration(45);

        taskManager.createTask(task3);
        taskManager.createTask(task4);
        taskManager.createTask(task5);

        List<Task> expectedTasks = List.of(task1, task2, task3, task4, task5);
        List<Task> actualTasks = taskManager.getTasks();

        assertEquals(expectedTasks, actualTasks);

        List<Task> expectedPrioritizedTasks = List.of(task5, task4, task3, task1, task2, subTask1, subTask2, subTask3);
        List<Task> actualPrioritizedTasks = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expectedPrioritizedTasks, actualPrioritizedTasks);
    }

    @Test
    void createTask_shouldThrowAnException_ifTasksIntersectInTimeAndStartTimeBetweenStartTimeAndEndTime() {
        Task task3 = createTask("Новая задача", "Описание задачи");
        task3.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30));
        task3.setDuration(15);

        Task task4 = createTask("Новая задача2", "Описание задачи");
        task4.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        task4.setDuration(15);

        Task task5 = createTask("Новая задача3", "Описание задачи");
        task5.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 40));
        task5.setDuration(30);

        taskManager.createTask(task3);
        taskManager.createTask(task4);

        TaskCreateOrUpdateException exception = assertThrows(
                TaskCreateOrUpdateException.class,
                () -> taskManager.createTask(task5)
        );
        assertEquals("Task execution time intersect with other tasks", exception.getMessage());

        List<Task> expectedTasks = List.of(task1, task2, task3, task4);
        List<Task> actualTasks = taskManager.getTasks();

        assertEquals(expectedTasks, actualTasks);

        List<Task> expectedPrioritizedTasks = List.of(task4, task3, task1, task2, subTask1, subTask2, subTask3);
        List<Task> actualPrioritizedTasks = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expectedPrioritizedTasks, actualPrioritizedTasks);
    }

    @Test
    void createTask_shouldThrowAnException_ifTasksIntersectInTimeAndEndTimeBetweenStartTimeAndEndTime() {
        Task task3 = createTask("Новая задача", "Описание задачи");
        task3.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30));
        task3.setDuration(15);

        Task task4 = createTask("Новая задача2", "Описание задачи");
        task4.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        task4.setDuration(15);

        Task task5 = createTask("Новая задача3", "Описание задачи");
        task5.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 20));
        task5.setDuration(20);

        taskManager.createTask(task3);
        taskManager.createTask(task4);

        TaskCreateOrUpdateException exception = assertThrows(
                TaskCreateOrUpdateException.class,
                () -> taskManager.createTask(task5)
        );
        assertEquals("Task execution time intersect with other tasks", exception.getMessage());

        List<Task> expectedTasks = List.of(task1, task2, task3, task4);
        List<Task> actualTasks = taskManager.getTasks();

        assertEquals(expectedTasks, actualTasks);

        List<Task> expectedPrioritizedTasks = List.of(task4, task3, task1, task2, subTask1, subTask2, subTask3);
        List<Task> actualPrioritizedTasks = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expectedPrioritizedTasks, actualPrioritizedTasks);
    }

    @Test
    void createTask_shouldThrowAnException_ifTasksIntersectInTimeAndStartTimeBeforeStartTimeAndEndTimeAfterEndTime() {
        Task task3 = createTask("Новая задача", "Описание задачи");
        task3.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30));
        task3.setDuration(15);

        Task task4 = createTask("Новая задача2", "Описание задачи");
        task4.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        task4.setDuration(15);

        Task task5 = createTask("Новая задача3", "Описание задачи");
        task5.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 20));
        task5.setDuration(30);

        taskManager.createTask(task3);
        taskManager.createTask(task4);

        TaskCreateOrUpdateException exception = assertThrows(
                TaskCreateOrUpdateException.class,
                () -> taskManager.createTask(task5)
        );
        assertEquals("Task execution time intersect with other tasks", exception.getMessage());

        List<Task> expectedTasks = List.of(task1, task2, task3, task4);
        List<Task> actualTasks = taskManager.getTasks();

        assertEquals(expectedTasks, actualTasks);

        List<Task> expectedPrioritizedTasks = List.of(task4, task3, task1, task2, subTask1, subTask2, subTask3);
        List<Task> actualPrioritizedTasks = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expectedPrioritizedTasks, actualPrioritizedTasks);
    }

    @Test
    void createTask_shouldThrowAnException_ifTasksIntersectInTimeAndStartTimeEqualStartTimeAndEndTimeEqualEndTime() {
        Task task3 = createTask("Новая задача", "Описание задачи");
        task3.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30));
        task3.setDuration(15);

        Task task4 = createTask("Новая задача2", "Описание задачи");
        task4.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        task4.setDuration(15);

        Task task5 = createTask("Новая задача3", "Описание задачи");
        task5.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30));
        task5.setDuration(15);

        taskManager.createTask(task3);
        taskManager.createTask(task4);

        TaskCreateOrUpdateException exception = assertThrows(
                TaskCreateOrUpdateException.class,
                () -> taskManager.createTask(task5)
        );
        assertEquals("Task execution time intersect with other tasks", exception.getMessage());

        List<Task> expectedTasks = List.of(task1, task2, task3, task4);
        List<Task> actualTasks = taskManager.getTasks();

        assertEquals(expectedTasks, actualTasks);

        List<Task> expectedPrioritizedTasks = List.of(task4, task3, task1, task2, subTask1, subTask2, subTask3);
        List<Task> actualPrioritizedTasks = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expectedPrioritizedTasks, actualPrioritizedTasks);
    }

    @Test
    void createEpic_shouldCreateEpic() {
        Epic epic3 = createEpic("Новый эпик", "Описание эпика");
        taskManager.createEpic(epic3);

        List<Epic> expected = List.of(epic1, epic2, epic3);
        List<Epic> actual = taskManager.getEpics();

        assertEquals(expected, actual);
    }

    @Test
    void updateTask_shouldAddTheTaskToThePrioritizedTasks_ifTasksDoesNotIntersectInTime() {
        Task task3 = createTask("Новая задача", "Описание задачи");
        task3.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30));
        task3.setDuration(15);

        task1 = taskManager.getTaskById(task1.getId());
        task1.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 0));
        task1.setDuration(30);

        task2 = taskManager.getTaskById(task2.getId());
        task2.setStartTime(LocalDateTime.of(2022, 12, 22, 10, 0));
        task2.setDuration(45);

        taskManager.createTask(task3);
        taskManager.updateTask(task1);
        taskManager.updateTask(task2);

        List<Task> expectedTasks = List.of(task1, task2, task3);
        List<Task> actualTasks = taskManager.getTasks();

        assertEquals(expectedTasks, actualTasks);

        List<Task> expectedPrioritizedTasks = List.of(task2, task1, task3, subTask1, subTask2, subTask3);
        List<Task> actualPrioritizedTasks = List.copyOf(taskManager.getPrioritizedTasks());

        assertEquals(expectedPrioritizedTasks, actualPrioritizedTasks);
    }

    private void createTestTasks() {
        task1 = createTask("Задача1", "Описание задачи");
        taskManager.createTask(task1);
        task2 = createTask("Задача2", "Описание задачи");
        taskManager.createTask(task2);
        epic1 = createEpic("Эпик1", "Описание эпика");
        taskManager.createEpic(epic1);
        epic2 = createEpic("Эпик2", "Описание эпика");
        taskManager.createEpic(epic2);
        subTask1 = createSubTask("Подзадача1", "Описание подзадачи", epic1);
        taskManager.createSubTask(subTask1);
        subTask2 = createSubTask("Подзадача2", "Описание подзадачи", epic1);
        taskManager.createSubTask(subTask2);
        subTask3 = createSubTask("Подзадача3", "Описание подзадачи", epic2);
        taskManager.createSubTask(subTask3);
    }

    private Task createTask(String name, String description) {
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setStatus(Status.NEW);
        return task;
    }

    private Epic createEpic(String name, String description) {
        Epic epic = new Epic();
        epic.setName(name);
        epic.setDescription(description);
        return epic;
    }

    private SubTask createSubTask(String name, String description, Epic epic) {
        SubTask subTask = new SubTask();
        subTask.setName(name);
        subTask.setDescription(description);
        subTask.setStatus(Status.NEW);
        subTask.setEpic(epic);
        return subTask;
    }
}