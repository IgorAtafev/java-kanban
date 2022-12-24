package ru.yandex.practicum.tasktracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.manager.InMemoryTaskManager;
import ru.yandex.practicum.tasktracker.manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;
    private SubTask subTask3;

    private final TaskManager taskManager = new InMemoryTaskManager();

    @BeforeEach
    void setUp() {
        createTestTasks();
    }

    @Test
    void getStatus_shouldChangeEpicStatusToNew_ifThereAreNoSubtasks() {
        assertTrue(taskManager.getEpicById(epic2.getId()).getStatus() == Status.NEW);
    }

    @Test
    void getStatus_shouldChangeEpicStatusToNew_ifStatusOfAllSubtasksChangedToNew() {
        subTask1.setStatus(Status.NEW);
        subTask2.setStatus(Status.NEW);
        subTask3.setStatus(Status.NEW);

        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        taskManager.updateSubTask(subTask3);

        assertTrue(taskManager.getEpicById(epic1.getId()).getStatus() == Status.NEW);
    }

    @Test
    void getStatus_shouldChangeEpicStatusToDone_ifStatusOfAllSubtasksChangedToDone() {
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.DONE);

        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        taskManager.updateSubTask(subTask3);

        assertTrue(taskManager.getEpicById(epic1.getId()).getStatus() == Status.DONE);
    }

    @Test
    void getStatus_shouldChangeEpicStatusToInProgress_ifStatusOfOneSubtaskChangedToInProgress() {
        subTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);
        assertTrue(taskManager.getEpicById(epic1.getId()).getStatus() == Status.IN_PROGRESS);
    }

    @Test
    void getStatus_shouldChangeEpicStatusToInProgress_ifStatusOfAllSubtasksAreDifferentAndNotInProgress() {
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.NEW);
        subTask3.setStatus(Status.DONE);

        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        taskManager.updateSubTask(subTask3);

        assertTrue(taskManager.getEpicById(epic1.getId()).getStatus() == Status.IN_PROGRESS);
    }

    @Test
    void setStatus_shouldThrowAnException() {
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> epic1.setStatus(Status.IN_PROGRESS)
        );
        assertEquals("Status setting is not supported for epic", exception.getMessage());
    }

    @Test
    void getStartTime_shouldSetEpicStartTimeToNull_ifThereAreNoSubtasks() {
        assertNull(taskManager.getEpicById(epic2.getId()).getStartTime());
    }

    @Test
    void getStartTime_shouldSetEpicStartTimeToNull_ifStartTimeOfAllSubtasksIsNull() {
        assertNull(taskManager.getEpicById(epic1.getId()).getStartTime());
    }

    @Test
    void getStartTime_shouldSetTheStartTimeOfTheEpicEqualToSubtaskStartTime_ifStartTimeOfOneSubtaskIsNotNull() {
        LocalDateTime startTime = LocalDateTime.of(2022, 12, 22, 11, 0);

        subTask3.setStartTime(startTime);
        assertEquals(startTime, taskManager.getEpicById(epic1.getId()).getStartTime());
    }

    @Test
    void getStartTime_shouldSetTheStartTimeOfTheEpicEqualToTheMinStartTimeOfAllSubtasks_ifStartTimeOfAllSubtasksIsNotNull() {
        LocalDateTime startTime1 = LocalDateTime.of(2022, 12, 22, 13, 30);
        LocalDateTime startTime2 = LocalDateTime.of(2022, 12, 22, 12, 0);
        LocalDateTime startTime3 = LocalDateTime.of(2022, 12, 22, 12, 23);

        LocalDateTime expected = startTime2;

        subTask1.setStartTime(startTime1);
        subTask2.setStartTime(startTime2);
        subTask3.setStartTime(startTime3);

        assertEquals(expected, taskManager.getEpicById(epic1.getId()).getStartTime());
    }

    @Test
    void setStartTime_shouldThrowAnException() {
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> epic1.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30))
        );
        assertEquals("Setting start time is not supported for epic", exception.getMessage());
    }

    @Test
    void getDuration_shouldSetEpicDurationToZero_ifThereAreNoSubtasks() {
        assertTrue(taskManager.getEpicById(epic2.getId()).getDuration() == 0);
    }

    @Test
    void getDuration_shouldSetEpicDurationToZero_ifDurationOfAllSubtasksIsZero() {
        assertTrue(taskManager.getEpicById(epic1.getId()).getDuration() == 0);
    }

    @Test
    void getDuration_shouldSetTheDurationOfTheEpicEqualToSubtaskDuration_ifDurationOfOneSubtaskIsNotZero() {
        int duration = 15;

        subTask3.setDuration(duration);
        assertTrue(taskManager.getEpicById(epic1.getId()).getDuration() == duration);
    }

    @Test
    void getDuration_shouldSetTheDurationOfTheEpicEqualToTheDurationOfAllSubtasks_ifDurationOfAllSubtasksIsNotZero() {
        int duration1 = 15;
        int duration2 = 30;
        int duration3 = 45;

        int expected = duration1 + duration2 + duration3;

        subTask1.setDuration(duration1);
        subTask2.setDuration(duration2);
        subTask3.setDuration(duration3);

        assertTrue(taskManager.getEpicById(epic1.getId()).getDuration() == expected);
    }

    @Test
    void setDuration_shouldThrowAnException() {
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> epic1.setDuration(30)
        );
        assertEquals("Setting duration is not supported for epic", exception.getMessage());
    }

    @Test
    void getEndTime_shouldSetEpicEndTimeToNull_ifThereAreNoSubtasks() {
        assertNull(taskManager.getEpicById(epic2.getId()).getEndTime());
    }

    @Test
    void getEndTime_shouldSetEpicEndTimeToNull_ifStartTimeOfAllSubtasksIsNull() {
        assertNull(taskManager.getEpicById(epic1.getId()).getEndTime());
    }

    @Test
    void getEndTime_shouldSetTheEndTimeOfTheEpicEqualToSubtaskEndTime_ifStartTimeOfOneSubtaskIsNotNull() {
        LocalDateTime startTime = LocalDateTime.of(2022, 12, 22, 11, 0);
        int duration = 15;

        LocalDateTime expected = startTime.plus(Duration.ofMinutes(duration));

        subTask3.setStartTime(startTime);
        subTask3.setDuration(duration);

        assertEquals(expected, taskManager.getEpicById(epic1.getId()).getEndTime());
    }

    @Test
    void getEndTime_shouldSetTheEndTimeOfTheEpicEqualToTheMaxEndTimeOfAllSubtasks_ifStartTimeOfAllSubtasksIsNotNull() {
        LocalDateTime startTime1 = LocalDateTime.of(2022, 12, 22, 12, 23);
        LocalDateTime startTime2 = LocalDateTime.of(2022, 12, 22, 13, 30);
        LocalDateTime startTime3 = LocalDateTime.of(2022, 12, 22, 12, 0);

        int duration1 = 15;
        int duration2 = 20;
        int duration3 = 40;

        LocalDateTime expected = startTime2.plus(Duration.ofMinutes(duration2));

        subTask1.setStartTime(startTime1);
        subTask2.setStartTime(startTime2);
        subTask3.setStartTime(startTime3);
        subTask1.setDuration(duration1);
        subTask2.setDuration(duration2);
        subTask3.setDuration(duration3);

        assertEquals(expected, taskManager.getEpicById(epic1.getId()).getEndTime());
    }

    private void createTestTasks() {
        epic1 = createEpic("Эпик1", "Описание эпика");
        taskManager.createEpic(epic1);
        epic2 = createEpic("Эпик2", "Описание эпика");
        taskManager.createEpic(epic2);
        subTask1 = createSubTask("Подзадача1", "Описание подзадачи", epic1);
        taskManager.createSubTask(subTask1);
        subTask2 = createSubTask("Подзадача2", "Описание подзадачи", epic1);
        taskManager.createSubTask(subTask2);
        subTask3 = createSubTask("Подзадача3", "Описание подзадачи", epic1);
        taskManager.createSubTask(subTask3);
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