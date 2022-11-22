package ru.yandex.practicum.tasktracker.manager;

import ru.yandex.practicum.tasktracker.model.Task;

import java.util.*;

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
    public void remove(int id) {
        removeNode(nodes.remove(id));
    }

    @Override
    public void removeAll(Set<Integer> ids) {
        for (int id : ids) {
            remove(id);
        }
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

    /**
     * Adds a task to the end of the linked list
     * @param task
     */
    private void linkLast(Task task) {
        final Node oldLast = last;
        final Node newNode = new Node(oldLast, task, null);

        last = newNode;
        if (oldLast == null) {
            first = newNode;
        } else {
            oldLast.next = newNode;
        }

        nodes.put(task.getId(), newNode);
    }

    /**
     * Removes a linked list node
     * @param node
     */
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

        node.item = null;
    }

    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task item, Node next) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }
}