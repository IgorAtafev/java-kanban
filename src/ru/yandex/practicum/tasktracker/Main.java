package ru.yandex.practicum.tasktracker;

import ru.yandex.practicum.tasktracker.manager.Managers;
import ru.yandex.practicum.tasktracker.manager.TaskManager;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Status;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        System.out.println("Тестирование...");

        System.out.println(System.lineSeparator());
        System.out.println("1. Создание задач:");
        for (int i = 1; i <= 2; i++) {
            taskManager.createTask(createTask("Задача" + i, "Описание задачи" + i));
            taskManager.createEpic(createEpic("Эпик" + i, "Описание эпика" + i));
        }

        taskManager.createSubTask(createSubTask(taskManager.getEpicById(2), "Подзадача1",
                "Описание подзадачи1"));
        taskManager.createSubTask(createSubTask(taskManager.getEpicById(2), "Подзадача2",
                "Описание подзадачи2"));
        taskManager.createSubTask(createSubTask(taskManager.getEpicById(4), "Подзадача3",
                "Описание подзадачи3"));

        System.out.printf("Список всех задач: %s", taskManager.getTasks());
        System.out.println();
        System.out.printf("Список всех эпиков: %s", taskManager.getEpics());
        System.out.println();
        System.out.printf("Список всех подзадач: %s", taskManager.getSubTasks());
        System.out.println();

        System.out.println(System.lineSeparator());
        System.out.println("2. Обновление задач:");
        taskManager.getTaskById(1).setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(taskManager.getTaskById(1));
        System.out.printf("Задача ID = 1 после изменения статуса: %s", taskManager.getTaskById(1));

        System.out.println();
        taskManager.getTaskById(3).setStatus(Status.DONE);
        taskManager.updateTask(taskManager.getTaskById(3));
        System.out.printf("Задача ID = 3 после изменения статуса: %s", taskManager.getTaskById(3));
        System.out.println();

        taskManager.getEpicById(2).setName("Обновленный эпик1");
        taskManager.getEpicById(2).setDescription("Описание обновленного эпика1");
        taskManager.updateEpic(taskManager.getEpicById(2));
        System.out.printf("Эпик ID = 2 после изменения названия и описания: %s",
                taskManager.getEpicById(2));
        System.out.println();

        taskManager.getSubTaskById(5).setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(taskManager.getSubTaskById(5));
        System.out.printf("Подзадача ID = 5 после изменения статуса на 'IN_PROGRESS': %s",
                taskManager.getSubTaskById(5));
        System.out.println();
        taskManager.getSubTaskById(6).setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(taskManager.getSubTaskById(6));
        System.out.printf("Подзадача ID = 6 после изменения статуса на 'IN_PROGRESS': %s",
                taskManager.getSubTaskById(6));
        System.out.println();
        System.out.printf("Эпик ID = 2 после изменения статуса его подзадач: %s",
                taskManager.getEpicById(2));
        System.out.println();

        taskManager.getSubTaskById(5).setStatus(Status.DONE);
        taskManager.updateSubTask(taskManager.getSubTaskById(5));
        System.out.printf("Подзадача ID = 5 после изменения статуса на 'DONE': %s",
                taskManager.getSubTaskById(5));
        System.out.println();
        taskManager.getSubTaskById(6).setStatus(Status.DONE);
        taskManager.updateSubTask(taskManager.getSubTaskById(6));
        System.out.printf("Подзадача ID = 6 после изменения статуса 'DONE': %s",
                taskManager.getSubTaskById(6));
        System.out.println();
        System.out.printf("Эпик ID = 2 после изменения статуса его подзадач: %s",
                taskManager.getEpicById(2));
        System.out.println();
        System.out.printf("Список всех подзадач после изменения статуса: %s", taskManager.getSubTasks());
        System.out.println();
        System.out.printf("Список всех подзадач эпика ID = 2: %s", taskManager.getSubTasksByEpic(2));
        System.out.println();

        System.out.println(System.lineSeparator());
        System.out.println("3. История просмотров задач:");
        System.out.println(taskManager.getHistoryManager().getHistory());
        System.out.println();

        System.out.println(System.lineSeparator());
        System.out.println("4. Удаление задач по идентификатору:");

        System.out.printf("Удаляем задачу ID = 1: %s", taskManager.getTaskById(1));
        System.out.println();
        taskManager.deleteTaskById(1);
        System.out.printf("Список всех задач: %s", taskManager.getTasks());
        System.out.println();

        System.out.printf("Удаляем подзадачу ID = 5: %s", taskManager.getSubTaskById(5));
        System.out.println();
        taskManager.deleteSubTaskById(5);
        System.out.printf("Список всех подзадач эпика ID = 2: %s", taskManager.getSubTasksByEpic(2));
        System.out.println();
        System.out.printf("Список всех подзадач: %s", taskManager.getSubTasks());
        System.out.println();

        System.out.printf("Удаляем эпик ID = 2: %s", taskManager.getEpicById(2));
        taskManager.deleteEpicById(2);
        System.out.println();
        System.out.printf("Список всех подзадач: %s", taskManager.getSubTasks());
        System.out.println();
        System.out.printf("Список всех эпиков: %s", taskManager.getEpics());
        System.out.println();

        System.out.println(System.lineSeparator());
        System.out.println("5. Удаление всех задач:");

        taskManager.deleteTasks();
        System.out.printf("Список всех задач после удаления: %s", taskManager.getTasks());
        System.out.println();

        System.out.printf("Эпик ID = 4: %s", taskManager.getEpicById(4));
        System.out.println();
        taskManager.getSubTaskById(7).setStatus(Status.DONE);
        taskManager.updateSubTask(taskManager.getSubTaskById(7));
        System.out.printf("Эпик ID = 4 после изменения статуса его подзадач: %s", taskManager.getEpicById(4));
        System.out.println();
        System.out.printf("Список всех подзадач эпика ID = 4: %s", taskManager.getSubTasksByEpic(4));
        System.out.println();

        taskManager.deleteEpics();
        System.out.printf("Список всех эпиков после удаления: %s", taskManager.getEpics());
        System.out.println();
        System.out.printf("Список всех подзадач: %s", taskManager.getSubTasks());
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
        task.setStatus(Status.NEW);
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
        subTask.setStatus(Status.NEW);
        subTask.setDescription(description);

        return subTask;
    }
}