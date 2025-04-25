package model.manager.inMemory;

import model.manager.HistoryManager;
import model.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node>  history = new HashMap<>();
    private Node head;
    private Node tail;

    private static class Node {
        Task task;
        Node next;
        Node prev;

        Node(Task task) {
            this.task = task;
            this.next = null;
            this.prev = null;
        }
    }

    @Override
    public void add(Task task) {
        var taskId = task.getId();
        if (history.containsKey(taskId)) {
            remove(taskId);
        }

        Node newNode = new Node(task);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        history.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        if (node == null) {
            return;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node current = head;
        while (current != null) {
            result.add(current.task);
            current = current.next;
        }
        return result;
    }

}
