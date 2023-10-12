package ru.yandex.practicum.tasktracker.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.tasktracker.client.KVTaskClient;
import ru.yandex.practicum.tasktracker.model.Epic;
import ru.yandex.practicum.tasktracker.model.SubTask;
import ru.yandex.practicum.tasktracker.model.Task;

import java.util.List;
import java.util.stream.Stream;

/**
 * Saves tasks and browsing history on the server and restores them from the server
 */
public class HttpTaskManager extends FileBackedTaskManager {

    private static final String TASKS_KEY = "tasks";
    private static final String EPICS_KEY = "epics";
    private static final String SUBTASKS_KEY = "subtasks";
    private static final String HISTORY_KEY = "history";

    private final KVTaskClient client;

    private final Gson defaultGson;
    private final Gson taskGson;
    private final Gson epicGson;
    private final Gson subTaskGson;

    private HttpTaskManager(String url) {
        client = new KVTaskClient(url);

        defaultGson = Managers.getDefaultGson();
        taskGson = Managers.getTaskGson(this);
        epicGson = Managers.getEpicGson(this);
        subTaskGson = Managers.getSubTaskGson(this);
    }

    /**
     * Restore manager data from server
     * @param url
     * @return task manager
     */
    public static HttpTaskManager load(String url) {
        HttpTaskManager taskManager = new HttpTaskManager(url);

        String tasksToJson = taskManager.client.load(TASKS_KEY);
        String epicsToJson = taskManager.client.load(EPICS_KEY);
        String subTasksToJson = taskManager.client.load(SUBTASKS_KEY);
        String historyToJson = taskManager.client.load(HISTORY_KEY);

        if (tasksToJson.isBlank() && epicsToJson.isBlank()) {
            return taskManager;
        }

        List<Task> tasks = taskManager.defaultGson.fromJson(tasksToJson, new TypeToken<List<Task>>(){}.getType());
        tasks.forEach(taskManager::updateTask);

        epicsToJson = epicsToJson.replaceFirst("\"subTasks\":\\[\\d+(,\\d+)*\\],", "");
        List<Epic> epics = taskManager.defaultGson.fromJson(epicsToJson, new TypeToken<List<Epic>>(){}.getType());
        epics.forEach(taskManager::updateEpic);

        List<SubTask> subTasks = taskManager.subTaskGson.fromJson(subTasksToJson,
                new TypeToken<List<SubTask>>(){}.getType());
        subTasks.forEach(taskManager::updateSubTask);

        List<Task> history = taskManager.taskGson.fromJson(historyToJson, new TypeToken<List<Task>>(){}.getType());
        history.forEach(taskManager.historyManager::add);

        taskManager.nextTaskId = Stream.of(tasks, epics, subTasks)
                .flatMap(List::stream)
                .map(Task::getId)
                .max(Integer::compareTo)
                .orElse(0);

        return taskManager;
    }

    @Override
    protected void save() {
        String tasks = defaultGson.toJson(getTasks());
        String epics = epicGson.toJson(getEpics());
        String subTasks = subTaskGson.toJson(getSubTasks());
        String history = taskGson.toJson(getHistory());

        client.put(TASKS_KEY, tasks);
        client.put(EPICS_KEY, epics);
        client.put(SUBTASKS_KEY, subTasks);
        client.put(HISTORY_KEY, history);
    }
}
