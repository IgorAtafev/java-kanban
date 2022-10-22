package ru.yandex.practicum.taskTracker;

public class Main {

    public static void main(String[] args) {

        TaskTracker taskTracker = new TaskTracker();

        System.out.println("Тестирование...");

        System.out.println(System.lineSeparator());
        System.out.println("1. Создание задач:");
        for (int i = 1; i <= 2; i++) {
            Task newTask = taskTracker.createTask(new Task("Задача" + i, "Описание задачи" + i));
            Epic newEpic = taskTracker.createEpic(new Epic("Эпик" + i, "Описание эпика" + i));
        }
        SubTask subTask1 = taskTracker.createSubTask(new SubTask(1, "Подзадача1",
                "Описание подзадачи1"));
        SubTask subTask2 = taskTracker.createSubTask(new SubTask(1, "Подзадача2",
                "Описание подзадачи2"));
        SubTask subTask3 = taskTracker.createSubTask(new SubTask(2, "Подзадача3",
                "Описание подзадачи3"));

        System.out.println(String.format("Список всех задач: %s", taskTracker.getTasks()));
        System.out.println(String.format("Список всех эпиков: %s", taskTracker.getEpics()));
        System.out.println(String.format("Список всех подзадач: %s", taskTracker.getSubTasks()));

        System.out.println(System.lineSeparator());
        System.out.println("2. Обновление задач:");
        taskTracker.getTaskById(1).setStatus(Status.IN_PROGRESS);
        Task updateTask1 = taskTracker.updateTask(taskTracker.getTaskById(1));
        System.out.println(String.format("Задача ID = 1 после изменения статуса: %s", taskTracker.getTaskById(1)));
        taskTracker.getTaskById(2).setStatus(Status.DONE);
        Task updateTask2 = taskTracker.updateTask(taskTracker.getTaskById(2));
        System.out.println(String.format("Задача ID = 2 после изменения статуса: %s", taskTracker.getTaskById(2)));

        taskTracker.getEpicById(1).setName("Обновленный эпик1");
        taskTracker.getEpicById(1).setDescription("Описание обновленного эпика1");
        Epic updateEpic = taskTracker.updateEpic(taskTracker.getEpicById(1));
        System.out.println(String.format("Эпик ID = 1 после изменения названия и описания: %s",
                taskTracker.getEpicById(1)));

        taskTracker.getSubTaskById(1).setStatus(Status.IN_PROGRESS);
        subTask1 = taskTracker.updateSubTask(taskTracker.getSubTaskById(1));
        System.out.println(String.format("Подзадача ID = 1 после изменения статуса на 'В работе': %s",
                taskTracker.getSubTaskById(1)));
        taskTracker.getSubTaskById(2).setStatus(Status.IN_PROGRESS);
        subTask2 = taskTracker.updateSubTask(taskTracker.getSubTaskById(2));
        System.out.println(String.format("Подзадача ID = 2 после изменения статуса на 'В работе': %s",
                taskTracker.getSubTaskById(2)));
        System.out.println(String.format("Эпик ID = 1 после изменения статуса его подзадач: %s",
                taskTracker.getEpicById(1)));

        taskTracker.getSubTaskById(1).setStatus(Status.DONE);
        subTask1 = taskTracker.updateSubTask(taskTracker.getSubTaskById(1));
        System.out.println(String.format("Подзадача ID = 1 после изменения статуса на 'Выполнена': %s",
                taskTracker.getSubTaskById(1)));
        taskTracker.getSubTaskById(2).setStatus(Status.DONE);
        subTask2 = taskTracker.updateSubTask(taskTracker.getSubTaskById(2));
        System.out.println(String.format("Подзадача ID = 2 после изменения статуса 'Выполнена': %s",
                taskTracker.getSubTaskById(2)));
        System.out.println(String.format("Эпик ID = 1 после изменения статуса его подзадач: %s",
                taskTracker.getEpicById(1)));

        System.out.println(System.lineSeparator());
        System.out.println("4. Удаление задач по идентификатору:");

        System.out.println(String.format("Удаленная задача ID = 1: %s", taskTracker.deleteTaskById(1)));
        System.out.println(String.format("Список всех задач: %s", taskTracker.getTasks()));

        System.out.println(String.format("Удаленная подзадача ID = 1: %s", taskTracker.deleteSubTaskById(1)));
        System.out.println(String.format("Список всех подзадач: %s", taskTracker.getSubTasks()));

        System.out.println(String.format("Удаленный эпик ID = 1: %s", taskTracker.deleteEpicById(1)));
        System.out.println(String.format("Список всех эпиков: %s", taskTracker.getEpics()));

        System.out.println(System.lineSeparator());
        System.out.println("5. Удаление всех задач:");

        taskTracker.deleteTasks();
        System.out.println(String.format("Список всех задач после удаления: %s", taskTracker.getTasks()));

        System.out.println(String.format("Эпик ID = 2: %s", taskTracker.getEpicById(2)));
        taskTracker.getSubTaskById(3).setStatus(Status.DONE);
        subTask3 = taskTracker.updateSubTask(taskTracker.getSubTaskById(3));
        System.out.println(String.format("Эпик ID = 2 после изменения статуса его подзадач: %s", taskTracker.getEpicById(2)));

        taskTracker.deleteSubTasksByEpic(taskTracker.getEpicById(2));
        System.out.println(String.format("Список подзадач эпика ID = 2 после их удаления: %s",
                taskTracker.getSubTasksByEpic(taskTracker.getEpicById(2))));
        System.out.println(String.format("Эпик ID = 2 после удаления всех его подзадач: %s", taskTracker.getEpicById(2)));

        taskTracker.deleteEpics();
        System.out.println(String.format("Список всех эпиков после удаления: %s", taskTracker.getEpics()));
    }
}