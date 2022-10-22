package ru.yandex.practicum.tasks;

import ru.yandex.practicum.tasks.util.ConsoleHelper;

public class Main {

    public static void main(String[] args) {

        TaskTracker taskTracker = new TaskTracker();
        ConsoleHelper consoleHelper = new ConsoleHelper();

        consoleHelper.writeMessage("Тестирование...");

        consoleHelper.writeMessage("1. Создание задач:");
        Task newTask1 = taskTracker.createTask(new Task("Задача1", "Описание задачи1"));
        Task newTask2 = taskTracker.createTask(new Task("Задача2", "Описание задачи2"));
        consoleHelper.writeMessage(String.format("Список задач: %s", taskTracker.getTasks().toString()));
        Epic newEpic1 = taskTracker.createEpic(new Epic("Эпик1", "Описание эпика1"));
        Epic newEpic2 = taskTracker.createEpic(new Epic("Эпик2", "Описание эпика2"));
        consoleHelper.writeMessage(String.format("Список эпиков: %s", taskTracker.getEpics().toString()));

        consoleHelper.writeMessage(System.lineSeparator());
        consoleHelper.writeMessage("2. Обновление задач:");
        newTask1.setStatus(Status.IN_PROGRESS);
        newTask2.setStatus(Status.DONE);
        Task updateTask1 = taskTracker.updateTask(newTask1);
        Task updateTask2 = taskTracker.updateTask(newTask2);
        consoleHelper.writeMessage(String.format("Список задач: %s", taskTracker.getTasks().toString()));

        consoleHelper.writeMessage(System.lineSeparator());
        consoleHelper.writeMessage("3. Получение задач по идентификатору:");
        consoleHelper.writeMessage("Задча ID = 2:");
        Task task = taskTracker.getTaskById(2);
        if (task == null) {
            consoleHelper.writeMessage("Задача не найдена!");
        } else {
            consoleHelper.writeMessage(String.format("Найденная задача: %s", task));
        }
        consoleHelper.writeMessage("Эпик ID = 3:");
        Epic epic = taskTracker.getEpicById(3);
        if (epic == null) {
            consoleHelper.writeMessage("Эпик не найден!");
        } else {
            consoleHelper.writeMessage(String.format("Найденный эпик: %s", epic));
        }

        consoleHelper.writeMessage(System.lineSeparator());
        consoleHelper.writeMessage("4. Удаление задач по идентификатору:");
        consoleHelper.writeMessage("Задча ID = 2:");
        task = taskTracker.deleteTaskById(2);
        if (task == null) {
            consoleHelper.writeMessage("Задача не найдена!");
        } else {
            consoleHelper.writeMessage(String.format("Удаленная задача: %s", task));
        }
        consoleHelper.writeMessage(String.format("Список задач: %s", taskTracker.getTasks().toString()));
        consoleHelper.writeMessage("Эпик ID = 3:");
        epic = taskTracker.deleteEpicById(3);
        if (epic == null) {
            consoleHelper.writeMessage("Эпик не найден!");
        } else {
            consoleHelper.writeMessage(String.format("Удаленный эпик: %s", epic));
        }
        consoleHelper.writeMessage(String.format("Список эпиков: %s", taskTracker.getEpics().toString()));

        consoleHelper.writeMessage(System.lineSeparator());
        consoleHelper.writeMessage("5. Удаление всех задач:");
        taskTracker.deleteTasks();
        consoleHelper.writeMessage(String.format("Список задач: %s", taskTracker.getTasks().toString()));
        taskTracker.deleteEpics();
        consoleHelper.writeMessage(String.format("Список эпиков: %s", taskTracker.getEpics().toString()));
    }
}