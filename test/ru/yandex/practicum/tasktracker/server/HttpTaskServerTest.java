package ru.yandex.practicum.tasktracker.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.manager.Managers;
import ru.yandex.practicum.tasktracker.manager.TaskManager;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.Status;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskServerTest {
    private static final String URL = "http://localhost:" + HttpTaskServer.PORT;

    private KVServer kvServer;
    private TaskManager taskManager;
    private Gson defaultGson;
    private Gson taskGson;
    private Gson epicGson;
    private Gson subTaskGson;
    private HttpTaskServer httpTaskServer;

    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;
    private SubTask subTask3;

    @BeforeEach
    void setUp() throws IOException {
        initTasks();
        kvServer = new KVServer();
        kvServer.start();

        taskManager = Managers.getDefault();
        defaultGson = Managers.getDefaultGson();
        taskGson = Managers.getTaskGson(taskManager);
        epicGson = Managers.getEpicGson(taskManager);
        subTaskGson = Managers.getSubTaskGson(taskManager);

        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    @AfterEach
    void serverStop() {
        httpTaskServer.stop();
        kvServer.stop();
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

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> expected = List.of(task1, task2, epic1, subTask1, subTask2);
        List<Task> actual = taskGson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertEquals(expected, actual);
    }

    @Test
    void getTasks_shouldReturnEmptyListOfTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = defaultGson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertTrue(tasks.isEmpty());
    }

    @Test
    void getTasks_shouldReturnListOfTasks() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> expected = List.of(task1, task2);
        List<Task> actual = defaultGson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void getEpics_shouldReturnEmptyListOfEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> epics = epicGson.fromJson(response.body(), new TypeToken<List<Epic>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertTrue(epics.isEmpty());
    }

    @Test
    void getEpics_shouldReturnListOfEpics() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> expected = List.of(epic1, epic2);
        List<Epic> actual = epicGson.fromJson(response.body(), new TypeToken<List<Epic>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void getSubTasks_shouldReturnEmptyListOfSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> subTasks = subTaskGson.fromJson(response.body(), new TypeToken<List<SubTask>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertTrue(subTasks.isEmpty());
    }

    @Test
    void getSubTasks_shouldReturnListOfSubtasks() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> expected = List.of(subTask1, subTask2, subTask3);
        List<SubTask> actual = subTaskGson.fromJson(response.body(), new TypeToken<List<SubTask>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void getSubTasksByEpic_shouldReturnListOfSubtasksByEpic() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/subtask/epic/?id=" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> expected = List.of(subTask1, subTask2);
        List<SubTask> actual = subTaskGson.fromJson(response.body(), new TypeToken<List<SubTask>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void getSubTasksByEpic_shouldReturnResponseEpicNotFound_ifTheEpicDoesNotExist()
            throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/subtask/epic/?id=100");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_NOT_FOUND, response.statusCode());
        assertEquals(HttpTaskServer.EPIC_NOT_FOUND, response.body());
    }

    @Test
    void getTaskById_shouldReturnTaskById() throws IOException, InterruptedException {
        taskManager.createTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/task/?id=" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task = defaultGson.fromJson(response.body(), Task.class);

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(task1, task);
    }

    @Test
    void getTaskById_shouldReturnResponseTaskNotFound_ifTaskDoesNotExist() throws IOException, InterruptedException {
        taskManager.createTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/task/?id=100");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_NOT_FOUND, response.statusCode());
        assertEquals(HttpTaskServer.TASK_NOT_FOUND, response.body());
    }

    @Test
    void getEpicById_shouldReturnEpicById() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/epic/?id=" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epic = epicGson.fromJson(response.body(), Epic.class);

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(epic1, epic);
    }

    @Test
    void getEpicById_shouldReturnResponseEpicNotFound_ifTheEpicDoesNotExist() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/epic/?id=100");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_NOT_FOUND, response.statusCode());
        assertEquals(HttpTaskServer.EPIC_NOT_FOUND, response.body());
    }

    @Test
    void getSubTaskById_shouldReturnSubtaskById() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/subtask/?id=" + subTask1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTask = subTaskGson.fromJson(response.body(), SubTask.class);

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(subTask1, subTask);
    }

    @Test
    void getSubTaskById_shouldReturnResponseSubtaskNotFound_ifSubtaskDoesNotExist()
            throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/subtask/?id=100");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_NOT_FOUND, response.statusCode());
        assertEquals(HttpTaskServer.SUBTASK_NOT_FOUND, response.body());
    }

    @Test
    void deleteTaskById_shouldRemoveTask() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/task/?id=" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(HttpTaskServer.TASK_DELETED_SUCCESSFULLY, response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/task/");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> expected = List.of(task2);
        List<Task> actual = defaultGson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void deleteTaskById_shouldReturnResponseTaskNotFound_ifTaskDoesNotExist() throws IOException, InterruptedException {
        taskManager.createTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/task/?id=100");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_NOT_FOUND, response.statusCode());
        assertEquals(HttpTaskServer.TASK_NOT_FOUND, response.body());
    }

    @Test
    void deleteEpicById_shouldRemoveEpic() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/epic/?id=" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(HttpTaskServer.EPIC_DELETED_SUCCESSFULLY, response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/epic/");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> expected = List.of(epic2);
        List<Epic> actual = epicGson.fromJson(response.body(), new TypeToken<List<Epic>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void deleteEpicById_shouldReturnResponseEpicNotFound_ifTheEpicDoesNotExist()
            throws IOException, InterruptedException {
        taskManager.createEpic(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/epic/?id=100");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_NOT_FOUND, response.statusCode());
        assertEquals(HttpTaskServer.EPIC_NOT_FOUND, response.body());
    }

    @Test
    void deleteSubTaskById_shouldRemoveSubtask() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/subtask/?id=" + subTask1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(HttpTaskServer.SUBTASK_DELETED_SUCCESSFULLY, response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/subtask/");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> expected = List.of(subTask2, subTask3);
        List<SubTask> actual = subTaskGson.fromJson(response.body(), new TypeToken<List<SubTask>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTaskById_shouldReturnResponseSubtaskNotFound_ifSubtaskDoesNotExist()
            throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/subtask/?id=100");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_NOT_FOUND, response.statusCode());
        assertEquals(HttpTaskServer.SUBTASK_NOT_FOUND, response.body());
    }

    @Test
    void deleteTasks_shouldRemoveAllTasks() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(HttpTaskServer.TASKS_DELETED_SUCCESSFULLY, response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/task/");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> expected = List.of();
        List<Task> actual = defaultGson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void deleteEpics_shouldRemoveAllEpics() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(HttpTaskServer.EPICS_DELETED_SUCCESSFULLY, response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/epic/");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> expected = List.of();
        List<Epic> actual = epicGson.fromJson(response.body(), new TypeToken<List<Epic>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void deleteSubTasks_shouldRemoveAllSubtasks() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(HttpTaskServer.SUBTASKS_DELETED_SUCCESSFULLY, response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/subtask/");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> expected = List.of();
        List<SubTask> actual = subTaskGson.fromJson(response.body(), new TypeToken<List<SubTask>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void createTask_shouldCreateATask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = defaultGson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertTrue(tasks.isEmpty());

        String taskToJson = defaultGson.toJson(task1);

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/task/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskToJson);
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_CREATED, response.statusCode());
        assertEquals(HttpTaskServer.TASK_CREATED_SUCCESSFULLY, response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/task/");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        tasks = defaultGson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(1, tasks.size());
    }

    @Test
    void createTask_shouldReturnResponseIncorrectJsonReceived_ifReceivedIncorrectJson()
            throws IOException, InterruptedException {
        String taskToJson = "{incorrect json}";

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/task/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskToJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_BAD_REQUEST, response.statusCode());
        assertEquals(HttpTaskServer.INCORRECT_JSON_RECEIVED, response.body());
    }

    @Test
    void createTask_shouldReturnResponseTimeIntersectWithOtherTasks_ifTasksIntersectInTime()
            throws IOException, InterruptedException {
        task1.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30));
        task1.setDuration(Duration.ofMinutes(15));
        taskManager.createTask(task1);

        task2.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 40));
        task2.setDuration(Duration.ofMinutes(30));

        String taskToJson = defaultGson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/task/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskToJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_BAD_REQUEST, response.statusCode());
        assertEquals("Task execution time intersect with other tasks", response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/task/");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> expected = List.of(task1);
        List<Task> actual = defaultGson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void createEpic_shouldCreateAnEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> epics = epicGson.fromJson(response.body(), new TypeToken<List<Epic>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertTrue(epics.isEmpty());

        String epicToJson = epicGson.toJson(epic1);

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/epic/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(epicToJson);
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_CREATED, response.statusCode());
        assertEquals(HttpTaskServer.EPIC_CREATED_SUCCESSFULLY, response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/epic/");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        epics = epicGson.fromJson(response.body(), new TypeToken<List<Epic>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(1, epics.size());
    }

    @Test
    void createEpic_shouldReturnResponseIncorrectJsonReceived_ifReceivedIncorrectJson()
            throws IOException, InterruptedException {
        String epicToJson = "{incorrect json}";

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/epic/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(epicToJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_BAD_REQUEST, response.statusCode());
        assertEquals(HttpTaskServer.INCORRECT_JSON_RECEIVED, response.body());
    }

    @Test
    void createSubTask_shouldCreateASubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> subTasks = subTaskGson.fromJson(response.body(), new TypeToken<List<SubTask>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertTrue(subTasks.isEmpty());

        taskManager.createEpic(epic1);

        String subTaskToJson = subTaskGson.toJson(subTask1);

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/subtask/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(subTaskToJson);
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_CREATED, response.statusCode());
        assertEquals(HttpTaskServer.SUBTASK_CREATED_SUCCESSFULLY, response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/subtask/");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        subTasks = subTaskGson.fromJson(response.body(), new TypeToken<List<SubTask>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(1, subTasks.size());
    }

    @Test
    void createSubTask_shouldReturnResponseIncorrectJsonReceived_ifReceivedIncorrectJson()
            throws IOException, InterruptedException {
        String subTaskToJson = "{incorrect json}";

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/subtask/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(subTaskToJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_BAD_REQUEST, response.statusCode());
        assertEquals(HttpTaskServer.INCORRECT_JSON_RECEIVED, response.body());
    }

    @Test
    void createSubTask_shouldReturnResponseTimeIntersectWithOtherTasks_ifTasksIntersectInTime()
            throws IOException, InterruptedException {
        taskManager.createEpic(epic1);

        subTask1.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 30));
        subTask1.setDuration(Duration.ofMinutes(15));
        taskManager.createSubTask(subTask1);

        subTask2.setStartTime(LocalDateTime.of(2022, 12, 22, 11, 40));
        subTask2.setDuration(Duration.ofMinutes(30));

        String subTaskToJson = subTaskGson.toJson(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/subtask/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(subTaskToJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_BAD_REQUEST, response.statusCode());
        assertEquals("Task execution time intersect with other tasks", response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/subtask/");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> expected = List.of(subTask1);
        List<SubTask> actual = subTaskGson.fromJson(response.body(), new TypeToken<List<SubTask>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void updateTask_shouldUpdateTheTask() throws IOException, InterruptedException {
        taskManager.createTask(task1);
        task1.setName("Обновленная задача");
        task1.setDescription("Описание обновленной задачи");

        String taskToJson = defaultGson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/task/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskToJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(HttpTaskServer.TASK_UPDATED_SUCCESSFULLY, response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/task/?id=" + task1.getId());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task = defaultGson.fromJson(response.body(), Task.class);

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals("Обновленная задача", task.getName());
        assertEquals("Описание обновленной задачи", task.getDescription());
    }

    @Test
    void updateTask_shouldReturnResponseTaskNotFound_ifTaskDoesNotExist() throws IOException, InterruptedException {
        taskManager.createTask(task1);

        Task task2 = createTask("Новая задача", "Описание задачи");
        task2.setId(2);

        String taskToJson = defaultGson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/task/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskToJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_BAD_REQUEST, response.statusCode());
        assertEquals(HttpTaskServer.TASK_NOT_FOUND, response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/task/");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> expected = List.of(task1);
        List<Task> actual = defaultGson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void updateEpic_shouldUpdateTheEpic() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        epic1.setName("Обновленный эпик");
        epic1.setDescription("Описание обновленного эпика");

        String epicToJson = epicGson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/epic/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(epicToJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(HttpTaskServer.EPIC_UPDATED_SUCCESSFULLY, response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/epic/?id=" + epic1.getId());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epic = epicGson.fromJson(response.body(), Epic.class);

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals("Обновленный эпик", epic.getName());
        assertEquals("Описание обновленного эпика", epic.getDescription());
    }

    @Test
    void updateEpic_shouldReturnResponseEpicNotFound_ifTheEpicDoesNotExist() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);

        Epic epic2 = createEpic("Новый эпик", "Описание эпика");
        epic2.setId(2);

        String epicToJson = epicGson.toJson(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/epic/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(epicToJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_BAD_REQUEST, response.statusCode());
        assertEquals(HttpTaskServer.EPIC_NOT_FOUND, response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/epic/");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> expected = List.of(epic1);
        List<Epic> actual = epicGson.fromJson(response.body(), new TypeToken<List<Epic>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void updateSubTask_shouldUpdateSubtask() throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);
        subTask1.setName("Обновленная подзадача");
        subTask1.setDescription("Описание обновленной подзадачи");

        String subTaskToJson = subTaskGson.toJson(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/subtask/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(subTaskToJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(HttpTaskServer.SUBTASK_UPDATED_SUCCESSFULLY, response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/subtask/?id=" + subTask1.getId());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTask = subTaskGson.fromJson(response.body(), SubTask.class);

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals("Обновленная подзадача", subTask.getName());
        assertEquals("Описание обновленной подзадачи", subTask.getDescription());
    }

    @Test
    void updateSubTask_shouldReturnResponseSubtaskNotFound_ifSubtaskDoesNotExist()
            throws IOException, InterruptedException {
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1);

        SubTask subTask2 = createSubTask("Новая подзадача", "Описание подзадачи", epic1);
        subTask2.setId(3);

        String subTaskToJson = subTaskGson.toJson(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/subtask/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(subTaskToJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_BAD_REQUEST, response.statusCode());
        assertEquals(HttpTaskServer.SUBTASK_NOT_FOUND, response.body());

        client = HttpClient.newHttpClient();
        uri = URI.create(URL + "/tasks/subtask/");
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> expected = List.of(subTask1);
        List<SubTask> actual = subTaskGson.fromJson(response.body(), new TypeToken<List<SubTask>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void getPrioritizedTasks_shouldReturnPrioritizedTasks() throws IOException, InterruptedException {
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

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> expected = List.of(subTask3, task3, task2, task1, subTask1, subTask2);
        List<Task> actual = taskGson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertEquals(HttpTaskServer.RESPONSE_CODE_OK, response.statusCode());
        assertEquals(expected, actual);
    }

    @Test
    void getResponseEndpointNotAllowed_shouldReturnResponseEndpointNotAllowed() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(URL + "/tasks/not_found/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpTaskServer.RESPONSE_CODE_METHOD_NOT_ALLOWED, response.statusCode());
        assertEquals(HttpTaskServer.ENDPOINT_NOT_ALLOWED, response.body());
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