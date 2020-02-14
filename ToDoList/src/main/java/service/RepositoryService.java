package service;

import main.model.Task;
import main.model.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class RepositoryService {

    @Autowired
    private TaskRepository taskRepository;

    public ResponseEntity getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        taskRepository.findAll().forEach(tasks::add);
        return new ResponseEntity(tasks, HttpStatus.OK);
    }

    public ResponseEntity addTask(Task task) {
        Task tempTask;
        synchronized (taskRepository) {
            tempTask = taskRepository.save(task);
        }
        return new ResponseEntity(tempTask, HttpStatus.CREATED);
    }

    public ResponseEntity getTask(int id) {
        try {
            Task task = taskRepository.findById(id).orElseThrow(IllegalArgumentException::new);
            return new ResponseEntity(task, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    public ResponseEntity putTask(int id, String name, String description) {
        try {
            Task task;
            synchronized (taskRepository) {
                task = taskRepository.findById(id).orElseThrow(IllegalArgumentException::new);
                task.setDescription(description);
                task.setName(name);
                taskRepository.save(task);
            }
            return new ResponseEntity(task, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    public ResponseEntity patchTask(int id, String name, String description) {
        try {
            Task task;
            synchronized (taskRepository) {
                task = taskRepository.findById(id).orElseThrow(IllegalArgumentException::new);
                if (!description.equals("")) {
                    task.setDescription(description);
                }
                if (!name.equals("")) {
                    task.setName(name);
                }
                taskRepository.save(task);
            }
            return new ResponseEntity(task, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    public ResponseEntity deleteTask(int id) {
        synchronized (taskRepository) {
            if (taskRepository.existsById(id)) {
                taskRepository.deleteById(id);
                return ResponseEntity.status(HttpStatus.OK).body(null);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
