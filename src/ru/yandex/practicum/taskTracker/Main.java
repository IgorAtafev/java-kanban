package ru.yandex.practicum.taskTracker;

public class Main {

    public static void main(String[] args) {

        TaskTracker taskTracker = new TaskTracker();

        System.out.println("Тестирование...");

        System.out.println("1. Создание задач:");
        for (int i = 1; i <= 3; i++) {
            Task newTask = taskTracker.createTask(new Task("Задача" + i, "Описание задачи" + i));
            Epic newEpic = taskTracker.createEpic(new Epic("Эпик" + i, "Описание эпика" + i));
        }
        SubTask subTask1 = taskTracker.createSubTask(new SubTask("Подзадача1",
                "Описание подзадачи1", 1));
        SubTask subTask2 = taskTracker.createSubTask(new SubTask("Подзадача2",
                "Описание подзадачи2", 1));
        SubTask subTask3 = taskTracker.createSubTask(new SubTask("Подзадача3",
                "Описание подзадачи3", 2));

        System.out.println(String.format("Список задач: %s", taskTracker.getTasks().toString()));
        System.out.println(String.format("Список эпиков: %s", taskTracker.getEpics().toString()));

        System.out.println(System.lineSeparator());
        System.out.println("2. Обновление задач:");
        Task task1 = taskTracker.getTaskById(1);
        task1.setStatus(Status.IN_PROGRESS);
        Task updateTask1 = taskTracker.updateTask(task1);
        Task task2 = taskTracker.getTaskById(2);
        task2.setStatus(Status.DONE);
        Task updateTask2 = taskTracker.updateTask(task2);
        System.out.println(String.format("Список задач: %s", taskTracker.getTasks().toString()));
        Epic epic1 = taskTracker.getEpicById(1);
        epic1.setName("Обновленный эпик1");
        epic1.setDescription("Описание обновленного эпика1");
        Task updateEpic1 = taskTracker.updateEpic(epic1);
        System.out.println(String.format("Список эпиков: %s", taskTracker.getEpics().toString()));

        System.out.println(System.lineSeparator());
        System.out.println("3. Получение задач по идентификатору:");
        for (int i = 1; i <= 3; i++) {
            System.out.println(String.format("Задача ID = %d:", i));
            Task task = taskTracker.getTaskById(i);
            if (task == null) {
                System.out.println("Задача не найдена!");
            } else {
                System.out.println(String.format("Найденная задача: %s", task));
            }

            System.out.println(String.format("Эпик ID = %d:", i));
            Epic epic = taskTracker.getEpicById(i);
            if (epic == null) {
                System.out.println("Эпик не найден!");
            } else {
                System.out.println(String.format("Найденный эпик: %s", epic));
            }

            System.out.println(String.format("Подзадача ID = %d:", i));
            SubTask subTask = taskTracker.getSubTaskById(i);
            if (subTask == null) {
                System.out.println("Подзадача не найдена!");
            } else {
                System.out.println(String.format("Найденная подзадача: %s", subTask));
            }
        }

        System.out.println(System.lineSeparator());
        System.out.println("4. Удаление задач по идентификатору:");
        for (int i = 1; i <= 2; i++) {
            System.out.println(String.format("Задача ID = %d:", i));
            Task task = taskTracker.deleteTaskById(i);
            if (task == null) {
                System.out.println("Задача не найдена!");
            } else {
                System.out.println(String.format("Удаленная задача: %s", task));
            }
            System.out.println(String.format("Список задач: %s", taskTracker.getTasks().toString()));

            System.out.println(String.format("Подзадача ID = %d:", i));
            SubTask subTask = taskTracker.deleteSubTaskById(i);
            if (subTask == null) {
                System.out.println("Подзадача не найдена!");
            } else {
                System.out.println(String.format("Удаленная подзадача: %s", subTask));
            }

            System.out.println(String.format("Эпик ID = %d:", i));
            Epic epic = taskTracker.deleteEpicById(i);
            if (epic == null) {
                System.out.println("Эпик не найден!");
            } else {
                System.out.println(String.format("Удаленный эпик: %s", epic));
            }

            System.out.println(String.format("Список эпиков: %s", taskTracker.getEpics().toString()));
        }

        System.out.println(System.lineSeparator());
        System.out.println("5. Удаление всех задач:");
        taskTracker.deleteTasks();
        System.out.println(String.format("Список задач: %s", taskTracker.getTasks().toString()));
        taskTracker.deleteEpics();
        System.out.println(String.format("Список эпиков: %s", taskTracker.getEpics().toString()));
    }
}