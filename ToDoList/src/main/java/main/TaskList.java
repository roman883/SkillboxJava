package main;

import main.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskList {

    private static AtomicInteger currentId = new AtomicInteger(1);
    private static ConcurrentHashMap<Integer, Task> taskList = new ConcurrentHashMap<>();

    public static List<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.addAll(taskList.values());
        return tasks;
    }

    public static int addTask(Task task) {
        int id = currentId.getAndIncrement();
        task.setId(id);
        taskList.put(id, task);
        return id;
    }

    public static Task getTask (int taskId) {
        if(taskList.containsKey(taskId)) {
            return taskList.get(taskId);
        }
        return null;
    }

    public static boolean deleteTask(int taskId) {
        if(taskList.containsKey(taskId)) {
            taskList.remove(taskId);
            return true;
        }
        return false;
    }
}