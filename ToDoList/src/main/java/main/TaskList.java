package main;

import response.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskList {

    private static int currentId = 1;
    private static HashMap<Integer, Task> taskList = new HashMap<>();

    public static List<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.addAll(taskList.values());
        return tasks;
    }

    public static int addTask(Task task) {
        int id = currentId++;
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