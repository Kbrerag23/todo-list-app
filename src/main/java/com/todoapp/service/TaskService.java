package com.todoapp.service;

import com.todoapp.model.Task;
import com.todoapp.model.User;
import com.todoapp.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    @Transactional
    public void updateTasksOrder(List<Long> taskIds, User user) {
        for (int i = 0; i < taskIds.size(); i++) {
            Long taskId = taskIds.get(i);
            Task task = taskRepository.findById(taskId).orElse(null);
            if (task != null && task.getUser().getId().equals(user.getId())) {
                task.setPosition(i);
                taskRepository.save(task);
            }
        }
    }

    // NUEVO: Se añade parámetro "search"
    public List<Task> getFilteredAndSortedTasks(User user, String status, String category, String sort, String search) {
        List<Task> tasks = taskRepository.findByUserOrderByPositionAscCreatedAtDesc(user);
        Stream<Task> stream = tasks.stream();

        // Filtro: Estado
        if ("completed".equalsIgnoreCase(status)) stream = stream.filter(Task::isCompleted);
        else if ("pending".equalsIgnoreCase(status)) stream = stream.filter(t -> !t.isCompleted());

        // Filtro: Etiqueta
        if (category != null && !category.isEmpty()) stream = stream.filter(t -> category.equalsIgnoreCase(t.getCategory()));

        // NUEVO Filtro: Búsqueda de texto
        if (search != null && !search.trim().isEmpty()) {
            String keyword = search.toLowerCase();
            stream = stream.filter(t -> t.getTitle().toLowerCase().contains(keyword) ||
                    (t.getDescription() != null && t.getDescription().toLowerCase().contains(keyword)));
        }

        // Filtro: Orden
        if ("newest".equalsIgnoreCase(sort)) stream = stream.sorted(Comparator.comparing(Task::getCreatedAt).reversed());
        else if ("oldest".equalsIgnoreCase(sort)) stream = stream.sorted(Comparator.comparing(Task::getCreatedAt));

        return stream.collect(Collectors.toList());
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

    private Task getTaskIfBelongsToUser(Long taskId, User user) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));
        if (!task.getUser().getId().equals(user.getId())) throw new SecurityException("Acceso denegado");
        return task;
    }

    public void updateTask(Long taskId, String newTitle, String newDescription, String newCategory, LocalDate newDueDate, User user) {
        Task task = getTaskIfBelongsToUser(taskId, user);
        task.setTitle(newTitle);
        task.setDescription(newDescription);
        task.setCategory(newCategory);
        task.setDueDate(newDueDate);
        taskRepository.save(task);
    }
}