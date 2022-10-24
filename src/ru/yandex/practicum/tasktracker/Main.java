package ru.yandex.practicum.tasktracker;

import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.manager.TaskTracker;

public class Main {

    public static void main(String[] args) {

        TaskTracker taskTracker = new TaskTracker();

        System.out.println("Тестирование...");

        System.out.println(System.lineSeparator());
        System.out.println("1. Создание задач:");
        for (int i = 1; i <= 2; i++) {
            taskTracker.createTask(createTask("Задача" + i, "Описание задачи" + i));
            taskTracker.createEpic(createEpic("Эпик" + i, "Описание эпика" + i));
        }

        taskTracker.createSubTask(createSubTask(taskTracker.getEpicById(1), "Подзадача1",
                "Описание подзадачи1"));
        taskTracker.createSubTask(createSubTask(taskTracker.getEpicById(1), "Подзадача2",
                "Описание подзадачи2"));
        taskTracker.createSubTask(createSubTask(taskTracker.getEpicById(2), "Подзадача3",
                "Описание подзадачи3"));

        System.out.printf("Список всех задач: %s", taskTracker.getTasks());
        System.out.println();
        System.out.printf("Список всех эпиков: %s", taskTracker.getEpics());
        System.out.println();
        System.out.printf("Список всех подзадач: %s", taskTracker.getSubTasks());
        System.out.println();

        System.out.println(System.lineSeparator());
        System.out.println("2. Обновление задач:");
        taskTracker.getTaskById(1).setStatus(Status.IN_PROGRESS);
        taskTracker.updateTask(taskTracker.getTaskById(1));
        System.out.printf("Задача ID = 1 после изменения статуса: %s", taskTracker.getTaskById(1));
        System.out.println();
        taskTracker.getTaskById(2).setStatus(Status.DONE);
        taskTracker.updateTask(taskTracker.getTaskById(2));
        System.out.printf("Задача ID = 2 после изменения статуса: %s", taskTracker.getTaskById(2));
        System.out.println();

        taskTracker.getEpicById(1).setName("Обновленный эпик1");
        taskTracker.getEpicById(1).setDescription("Описание обновленного эпика1");
        taskTracker.updateEpic(taskTracker.getEpicById(1));
        System.out.printf("Эпик ID = 1 после изменения названия и описания: %s",
                taskTracker.getEpicById(1));
        System.out.println();

        taskTracker.getSubTaskById(1).setStatus(Status.IN_PROGRESS);
        taskTracker.updateSubTask(taskTracker.getSubTaskById(1));
        System.out.printf("Подзадача ID = 1 после изменения статуса на 'IN_PROGRESS': %s",
                taskTracker.getSubTaskById(1));
        System.out.println();
        taskTracker.getSubTaskById(2).setStatus(Status.IN_PROGRESS);
        taskTracker.updateSubTask(taskTracker.getSubTaskById(2));
        System.out.printf("Подзадача ID = 2 после изменения статуса на 'IN_PROGRESS': %s",
                taskTracker.getSubTaskById(2));
        System.out.println();
        System.out.printf("Эпик ID = 1 после изменения статуса его подзадач: %s",
                taskTracker.getEpicById(1));
        System.out.println();

        taskTracker.getSubTaskById(1).setStatus(Status.DONE);
        taskTracker.updateSubTask(taskTracker.getSubTaskById(1));
        System.out.printf("Подзадача ID = 1 после изменения статуса на 'DONE': %s",
                taskTracker.getSubTaskById(1));
        System.out.println();
        taskTracker.getSubTaskById(2).setStatus(Status.DONE);
        taskTracker.updateSubTask(taskTracker.getSubTaskById(2));
        System.out.printf("Подзадача ID = 2 после изменения статуса 'DONE': %s",
                taskTracker.getSubTaskById(2));
        System.out.println();
        System.out.printf("Эпик ID = 1 после изменения статуса его подзадач: %s",
                taskTracker.getEpicById(1));
        System.out.println();
        System.out.printf("Список всех подзадач после изменения статуса: %s", taskTracker.getSubTasks());
        System.out.println();

        System.out.println(System.lineSeparator());
        System.out.println("4. Удаление задач по идентификатору:");

        System.out.printf("Удаляем задачу ID = 1: %s", taskTracker.getTaskById(1));
        System.out.println();
        taskTracker.deleteTaskById(1);
        System.out.printf("Список всех задач: %s", taskTracker.getTasks());
        System.out.println();

        System.out.printf("Удаляем подзадачу ID = 1: %s", taskTracker.getSubTaskById(1));
        System.out.println();
        taskTracker.deleteSubTaskById(1);
        System.out.printf("Список всех подзадач: %s", taskTracker.getSubTasks());
        System.out.println();

        System.out.printf("Удаляем эпик ID = 1: %s", taskTracker.getEpicById(1));
        taskTracker.deleteEpicById(1);
        System.out.println();
        System.out.printf("Список всех эпиков: %s", taskTracker.getEpics());
        System.out.println();
        System.out.printf("Список всех подзадач: %s", taskTracker.getSubTasks());
        System.out.println();

        System.out.println(System.lineSeparator());
        System.out.println("5. Удаление всех задач:");

        taskTracker.deleteTasks();
        System.out.printf("Список всех задач после удаления: %s", taskTracker.getTasks());
        System.out.println();

        System.out.printf("Эпик ID = 2: %s", taskTracker.getEpicById(2));
        System.out.println();
        taskTracker.getSubTaskById(3).setStatus(Status.DONE);
        taskTracker.updateSubTask(taskTracker.getSubTaskById(3));
        System.out.printf("Эпик ID = 2 после изменения статуса его подзадач: %s", taskTracker.getEpicById(2));
        System.out.println();

        taskTracker.deleteSubTasksByEpic(taskTracker.getEpicById(2));
        System.out.printf("Список подзадач эпика ID = 2 после их удаления: %s",
                taskTracker.getSubTasksByEpic(taskTracker.getEpicById(2)));
        System.out.println();
        System.out.printf("Эпик ID = 2 после удаления всех его подзадач: %s", taskTracker.getEpicById(2));
        System.out.println();
        System.out.printf("Список всех подзадач: %s", taskTracker.getSubTasks());
        System.out.println();

        taskTracker.deleteEpics();
        System.out.printf("Список всех эпиков после удаления: %s", taskTracker.getEpics());
        System.out.println();
        System.out.printf("Список всех подзадач: %s", taskTracker.getSubTasks());
        System.out.println();
    }

    /**
     * Creates a task
     * @param name
     * @param description
     */
    private static Task createTask(String name, String description) {
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);

        return task;
    }

    /**
     * Creates a epic
     * @param name
     * @param description
     */
    private static Epic createEpic(String name, String description) {
        Epic epic = new Epic();
        epic.setName(name);
        epic.setDescription(description);

        return epic;
    }

    /**
     * Creates a subTask
     * @param name
     * @param description
     */
    private static SubTask createSubTask(Epic epic, String name, String description) {
        SubTask subTask = new SubTask();
        subTask.setEpic(epic);
        subTask.setName(name);
        subTask.setDescription(description);

        return subTask;
    }
}