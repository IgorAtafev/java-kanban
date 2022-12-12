package ru.yandex.practicum.tasktracker.manager;

import ru.yandex.practicum.tasktracker.model.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * Class implements the work of the task history in the form of a doubly linked list.
 * Only the last view of the task should be displayed in the history.
 * The previous view should be deleted immediately after the new one appears - in O(1)
 */
public class InMemoryHistoryManager implements HistoryManager {
    private Node first;
    private Node last;
    private final Map<Integer, Node> nodes = new HashMap<>();

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int taskId) {
        removeNode(nodes.remove(taskId));
    }

    @Override
    public void removeAll(Set<Integer> taskIds) {
        taskIds.forEach(this::remove);
    }

    @Override
    public List<Task> getHistory() {
        final List<Task> history = new ArrayList<>();

        Node current = first;
        while (current != null) {
            history.add(current.item);
            current = current.next;
        }

        return history;
    }

    private void linkLast(Task task) {
        final Node oldLast = last;
        final Node newNode = new Node(task);

        newNode.prev = oldLast;
        last = newNode;
        if (oldLast == null) {
            first = newNode;
        } else {
            oldLast.next = newNode;
        }

        nodes.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        final Node next = node.next;
        final Node prev = node.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
    }

    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Task item) {
            this.item = item;
        }
    }
}