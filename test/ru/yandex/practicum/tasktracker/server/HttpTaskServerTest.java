package ru.yandex.practicum.tasktracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.manager.InMemoryTaskManager;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Status;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;
import ru.yandex.practicum.tasktracker.util.TaskAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskServerTest {
    private static final String URL = "http://localhost:8080";

    private static final int RESPONSE_CODE_OK = 200;
    private static final int RESPONSE_CODE_CREATED = 201;
    private static final int RESPONSE_CODE_BAD_REQUEST = 400;
    private static final int RESPONSE_CODE_NOT_FOUND = 404;

    private static final String RESPONSE_BODY_TASK_NOT_FOUND = "Task with the specified ID was not found";
    private static final String RESPONSE_BODY_EPIC_NOT_FOUND = "Epic with the specified ID was not found";
    private static final String RESPONSE_BODY_SUBTASK_NOT_FOUND = "Subtask with the specified ID was not found";
    private static final String RESPONSE_BODY_TASK_DELETED_SUCCESSFULLY = "Task deleted successfully";
    private static final String RESPONSE_BODY_EPIC_DELETED_SUCCESSFULLY = "Epic deleted successfully";
    private static final String RESPONSE_BODY_SUBTASK_DELETED_SUCCESSFULLY = "Subtask deleted successfully";
    private static final String RESPONSE_BODY_TASKS_DELETED_SUCCESSFULLY = "All tasks deleted successfully";
    private static final String RESPONSE_BODY_EPICS_DELETED_SUCCESSFULLY = "All epics deleted successfully";
    private static final String RESPONSE_BODY_SUBTASKS_DELETED_SUCCESSFULLY = "All subtasks deleted successfully";

    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;
    private SubTask subTask3;

    private final InMemoryTaskManager taskManager = new InMemoryTaskManager();
    private HttpTaskServer server;
    private HttpClient client;

    private final Gson gsonDefault = new Gson();
    private final Gson gsonTask = new GsonBuilder()
            .registerTypeAdapter(Task.class, new TaskAdapter(taskManager))
            .create();
    private final Gson gsonEpic = new GsonBuilder()
            .registerTypeAdapter(SubTask.class, new TaskAdapter(taskManager))
            .create();
    private final Gson gsonSubTask = new GsonBuilder()
            .registerTypeAdapter(Epic.class, new TaskAdapter(taskManager))
            .create();

    @BeforeEach
    void setUp() throws IOException {
        initTasks();
        server = new HttpTaskServer(taskManager);
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void serverStop() {
        server.stop();
    }

    @Test
    void getHistory_shouldReturnHistory() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask1.getId());
        taskManager.getSubTaskById(subTask2.getId());

        URI url = URI.create(URL + "/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> expected = List.of(task1, task2, epic1, subTask1, subTask2);
        List<Task> actual = gsonTask.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());

        assertEquals(expected, actual);
    }

    @Test
    void getTasks_shouldReturnEmptyListOfTasks() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = gsonDefault.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());

        assertEquals(RESPONSE_CODE_OK, response.statusCode());
        assertTrue(tasks.isEmpty());
    }

    @Test
    void getTasks_shouldReturnListOfTasks() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        URI url = URI.create(URL + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> expected = List.of(task1, task2);
        List<Task> actual = gsonDefault.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());

        assertEquals(RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void getEpics_shouldReturnEmptyListOfEpics() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> epics = gsonEpic.fromJson(response.body(), new TypeToken<ArrayList<Epic>>(){}.getType());

        assertEquals(RESPONSE_CODE_OK, response.statusCode());
        assertTrue(epics.isEmpty());
    }

    @Test
    void getEpics_shouldReturnListOfEpics() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        URI url = URI.create(URL + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> expected = List.of(epic1, epic2);
        List<Epic> actual = gsonEpic.fromJson(response.body(), new TypeToken<ArrayList<Epic>>(){}.getType());

        assertEquals(RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void getSubTasks_shouldReturnEmptyListOfSubtasks() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> subTasks = gsonSubTask.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>(){}.getType());

        assertEquals(RESPONSE_CODE_OK, response.statusCode());
        assertTrue(subTasks.isEmpty());
    }

    @Test
    void getSubTasks_shouldReturnListOfSubtasks() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        URI url = URI.create(URL + "/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> expected = List.of(subTask1, subTask2, subTask3);
        List<SubTask> actual = gsonSubTask.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>(){}.getType());

        assertEquals(RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void getSubTasksByEpic_shouldReturnListOfSubtasksByEpic() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        URI url = URI.create(URL + "/tasks/subtask/epic/?id=" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> expected = List.of(subTask1, subTask2);
        List<SubTask> actual = gsonSubTask.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>(){}.getType());

        assertEquals(RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void getSubTasksByEpic_shouldReturnResponseEpicNotFound_ifTheEpicDoesNotExist()
            throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        URI url = URI.create(URL + "/tasks/subtask/epic/?id=100");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(RESPONSE_CODE_NOT_FOUND, response.statusCode());
        assertEquals(RESPONSE_BODY_EPIC_NOT_FOUND, response.body());
    }

    @Test
    void getTaskById_shouldReturnTaskById() throws IOException, InterruptedException {
        taskManager.createTask(task1);

        URI url = URI.create(URL + "/tasks/task/?id=" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task = gsonDefault.fromJson(response.body(), Task.class);

        assertEquals(RESPONSE_CODE_OK, response.statusCode());
        assertEquals(task1, task);
    }

    @Test
    void getTaskById_shouldReturnResponseTaskNotFound_ifTaskDoesNotExist() throws IOException, InterruptedException {
        taskManager.createTask(task1);

        URI url = URI.create(URL + "/tasks/task/?id=100");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(RESPONSE_CODE_NOT_FOUND, response.statusCode());
        assertEquals(RESPONSE_BODY_TASK_NOT_FOUND, response.body());
    }

    @Test
    void getEpicById_shouldReturnEpicById() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);

        URI url = URI.create(URL + "/tasks/epic/?id=" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epic = gsonEpic.fromJson(response.body(), Epic.class);

        assertEquals(RESPONSE_CODE_OK, response.statusCode());
        assertEquals(epic1, epic);
    }

    @Test
    void getEpicById_shouldReturnResponseEpicNotFound_ifTheEpicDoesNotExist() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);

        URI url = URI.create(URL + "/tasks/epic/?id=100");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(RESPONSE_CODE_NOT_FOUND, response.statusCode());
        assertEquals(RESPONSE_BODY_EPIC_NOT_FOUND, response.body());
    }

    @Test
    void getSubTaskById_shouldReturnSubtaskById() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);

        URI url = URI.create(URL + "/tasks/subtask/?id=" + subTask1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTask = gsonSubTask.fromJson(response.body(), SubTask.class);

        assertEquals(RESPONSE_CODE_OK, response.statusCode());
        assertEquals(subTask1, subTask);
    }

    @Test
    void getSubTaskById_shouldReturnResponseSubtaskNotFound_ifSubtaskDoesNotExist()
            throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);

        URI url = URI.create(URL + "/tasks/subtask/?id=100");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(RESPONSE_CODE_NOT_FOUND, response.statusCode());
        assertEquals(RESPONSE_BODY_SUBTASK_NOT_FOUND, response.body());
    }

    @Test
    void deleteTaskById_shouldRemoveTask() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        URI url = URI.create(URL + "/tasks/task/?id=" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(RESPONSE_CODE_OK, response.statusCode());
        assertEquals(RESPONSE_BODY_TASK_DELETED_SUCCESSFULLY, response.body());

        url = URI.create(URL + "/tasks/task/");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> expected = List.of(task2);
        List<Task> actual = gsonDefault.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());

        assertEquals(RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void deleteTaskById_shouldReturnResponseTaskNotFound_ifTaskDoesNotExist() throws IOException, InterruptedException {
        taskManager.createTask(task1);

        URI url = URI.create(URL + "/tasks/task/?id=100");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(RESPONSE_CODE_NOT_FOUND, response.statusCode());
        assertEquals(RESPONSE_BODY_TASK_NOT_FOUND, response.body());
    }

    @Test
    void deleteEpicById_shouldRemoveEpic() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        URI url = URI.create(URL + "/tasks/epic/?id=" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(RESPONSE_CODE_OK, response.statusCode());
        assertEquals(RESPONSE_BODY_EPIC_DELETED_SUCCESSFULLY, response.body());

        url = URI.create(URL + "/tasks/epic/");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> expected = List.of(epic2);
        List<Epic> actual = gsonEpic.fromJson(response.body(), new TypeToken<ArrayList<Epic>>(){}.getType());

        assertEquals(RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void deleteEpicById_shouldReturnResponseEpicNotFound_ifTheEpicDoesNotExist()
            throws IOException, InterruptedException {
        taskManager.createEpic(epic1);

        URI url = URI.create(URL + "/tasks/epic/?id=100");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(RESPONSE_CODE_NOT_FOUND, response.statusCode());
        assertEquals(RESPONSE_BODY_EPIC_NOT_FOUND, response.body());
    }

    @Test
    void deleteSubTaskById_shouldRemoveSubtask() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        URI url = URI.create(URL + "/tasks/subtask/?id=" + subTask1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(RESPONSE_CODE_OK, response.statusCode());
        assertEquals(RESPONSE_BODY_SUBTASK_DELETED_SUCCESSFULLY, response.body());

        url = URI.create(URL + "/tasks/subtask/");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> expected = List.of(subTask2, subTask3);
        List<SubTask> actual = gsonSubTask.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>(){}.getType());

        assertEquals(RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTaskById_shouldReturnResponseSubtaskNotFound_ifSubtaskDoesNotExist()
            throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);

        URI url = URI.create(URL + "/tasks/subtask/?id=100");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(RESPONSE_CODE_NOT_FOUND, response.statusCode());
        assertEquals(RESPONSE_BODY_SUBTASK_NOT_FOUND, response.body());
    }

    private void initTasks() {
        task1 = createTask("Задача1", "Описание задачи");
        task2 = createTask("Задача2", "Описание задачи");
        epic1 = createEpic("Эпик1", "Описание эпика");
        epic2 = createEpic("Эпик2", "Описание эпика");
        subTask1 = createSubTask("Подзадача1", "Описание подзадачи", epic1);
        subTask2 = createSubTask("Подзадача2", "Описание подзадачи", epic1);
        subTask3 = createSubTask("Подзадача3", "Описание подзадачи", epic2);
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