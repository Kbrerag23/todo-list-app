package com.todoapp.service;

import com.todoapp.model.Task;
import com.todoapp.model.User;
import com.todoapp.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    @Transactional
    public void updateTasksOrder(List<Long> taskIds, User user) {
        for (int i = 0; i < taskIds.size(); i++) {
            Long taskId = taskIds.get(i);
            Task task = taskRepository.findById(taskId).orElse(null);

            // Verificamos que la tarea exista y sea del usuario logueado
            if (task != null && task.getUser().getId().equals(user.getId())) {
                task.setPosition(i); // El índice 'i' será la nueva posición (0, 1, 2...)
                taskRepository.save(task);
            }
        }
    }

    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByUserOrderByPositionAscCreatedAtDesc(user);
    }

    public void saveTask(Task task, User user) {
        task.setUser(user);
        taskRepository.save(task);
    }

    public void toggleTaskStatus(Long taskId, User user) {
        Task task = getTaskIfBelongsToUser(taskId, user);
        task.setCompleted(!task.isCompleted());
        taskRepository.save(task);
    }

    public void deleteTask(Long taskId, User user) {
        Task task = getTaskIfBelongsToUser(taskId, user);
        taskRepository.delete(task);
    }

    // Seguridad adicional a nivel de servicio: garantizar que la tarea es del usuario
    private Task getTaskIfBelongsToUser(Long taskId, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));
        if (!task.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Acceso denegado");
        }
        return task;
    }

    public void updateTask(Long taskId, String newTitle, String newDescription, User user) {
        Task task = getTaskIfBelongsToUser(taskId, user);
        task.setTitle(newTitle);
        task.setDescription(newDescription);
        taskRepository.save(task);
    }


}