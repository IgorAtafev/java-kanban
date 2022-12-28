package ru.yandex.practicum.tasktracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @BeforeEach
    void setUp() {
        createTestTasks();
    }

    @Test
    void getStatus_shouldChangeEpicStatusToNew_ifThereAreNoSubtasks() {
        assertTrue(epic2.getStatus() == Status.NEW);
    }

    @Test
    void getStatus_shouldChangeEpicStatusToNew_ifStatusOfAllSubtasksChangedToNew() {
        subTask1.setStatus(Status.NEW);
        subTask2.setStatus(Status.NEW);
        subTask3.setStatus(Status.NEW);

        assertTrue(epic1.getStatus() == Status.NEW);
    }

    @Test
    void getStatus_shouldChangeEpicStatusToDone_ifStatusOfAllSubtasksChangedToDone() {
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.DONE);

        assertTrue(epic1.getStatus() == Status.DONE);
    }

    @Test
    void getStatus_shouldChangeEpicStatusToInProgress_ifStatusOfOneSubtaskChangedToInProgress() {
        subTask1.setStatus(Status.IN_PROGRESS);
        assertTrue(epic1.getStatus() == Status.IN_PROGRESS);
    }

    @Test
    void getStatus_shouldChangeEpicStatusToInProgress_ifStatusOfAllSubtasksAreDifferentAndNotInProgress() {
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.NEW);
        subTask3.setStatus(Status.DONE);

        assertTrue(epic1.getStatus() == Status.IN_PROGRESS);
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
    void getStartTime_shouldCheckForNull_ifThereAreNoSubtasks() {
        assertNull(epic2.getStartTime());
    }

    @Test
    void getStartTime_shouldCheckForNull_ifStartTimeOfAllSubtasksIsNull() {
        assertNull(epic1.getStartTime());
    }

    @Test
    void getStartTime_shouldSetTheStartTimeOfTheEpicEqualToSubtaskStartTime_ifStartTimeOfOneSubtaskIsNotNull() {
        LocalDateTime startTime = LocalDateTime.of(2022, 12, 22, 11, 0);
        subTask3.setStartTime(startTime);
        assertEquals(startTime, epic1.getStartTime());
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

        assertEquals(expected, epic1.getStartTime());
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
    void getDuration_shouldCheckForNull_ifThereAreNoSubtasks() {
        assertNull(epic2.getDuration());
    }

    @Test
    void getDuration_shouldCheckForNull_ifDurationOfAllSubtasksIsNull() {
        assertNull(epic1.getDuration());
    }

    @Test
    void getDuration_shouldSetTheDurationOfTheEpicEqualToSubtaskDuration_ifDurationOfOneSubtaskIsNotNull() {
        Duration duration = Duration.ofMinutes(15);
        subTask3.setDuration(duration);
        assertEquals(duration, epic1.getDuration());
    }

    @Test
    void getDuration_shouldSetTheDurationOfTheEpicEqualToTheDurationOfAllSubtasks_ifDurationOfAllSubtasksIsNotNull() {
        Duration duration1 = Duration.ofMinutes(15);
        Duration duration2 = Duration.ofMinutes(30);
        Duration duration3 = Duration.ofMinutes(45);

        Duration expected = duration1.plus(duration2).plus(duration3);

        subTask1.setDuration(duration1);
        subTask2.setDuration(duration2);
        subTask3.setDuration(duration3);

        assertEquals(expected, epic1.getDuration());
    }

    @Test
    void setDuration_shouldThrowAnException() {
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> epic1.setDuration(Duration.ofMinutes(30))
        );
        assertEquals("Setting duration is not supported for epic", exception.getMessage());
    }

    @Test
    void getEndTime_shouldCheckForNull_ifThereAreNoSubtasks() {
        assertNull(epic2.getEndTime());
    }

    @Test
    void getEndTime_shouldCheckForNull_ifStartTimeOfAllSubtasksIsNull() {
        assertNull(epic1.getEndTime());
    }

    @Test
    void getEndTime_shouldSetTheEndTimeOfTheEpicEqualToSubtaskEndTime_ifStartTimeOfOneSubtaskIsNotNull() {
        LocalDateTime startTime = LocalDateTime.of(2022, 12, 22, 11, 0);
        Duration duration = Duration.ofMinutes(15);

        LocalDateTime expected = startTime.plus(duration);

        subTask3.setStartTime(startTime);
        subTask3.setDuration(duration);

        assertEquals(expected, epic1.getEndTime());
    }

    @Test
    void getEndTime_shouldSetTheEndTimeOfTheEpicEqualToTheMaxEndTimeOfAllSubtasks_ifStartTimeOfAllSubtasksIsNotNull() {
        LocalDateTime startTime1 = LocalDateTime.of(2022, 12, 22, 12, 23);
        LocalDateTime startTime2 = LocalDateTime.of(2022, 12, 22, 13, 30);
        LocalDateTime startTime3 = LocalDateTime.of(2022, 12, 22, 12, 0);

        Duration duration1 = Duration.ofMinutes(15);
        Duration duration2 = Duration.ofMinutes(20);
        Duration duration3 = Duration.ofMinutes(40);

        LocalDateTime expected = startTime2.plus(duration2);

        subTask1.setStartTime(startTime1);
        subTask2.setStartTime(startTime2);
        subTask3.setStartTime(startTime3);
        subTask1.setDuration(duration1);
        subTask2.setDuration(duration2);
        subTask3.setDuration(duration3);

        assertEquals(expected, epic1.getEndTime());
    }

    private void createTestTasks() {
        epic1 = createEpic("Эпик1", "Описание эпика");
        epic2 = createEpic("Эпик2", "Описание эпика");
        subTask1 = createSubTask("Подзадача1", "Описание подзадачи", epic1);
        subTask2 = createSubTask("Подзадача2", "Описание подзадачи", epic1);
        subTask3 = createSubTask("Подзадача3", "Описание подзадачи", epic1);
        epic1.addSubTask(subTask1);
        epic1.addSubTask(subTask2);
        epic1.addSubTask(subTask3);
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