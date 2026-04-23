package com.todoapp.controller;

import com.todoapp.model.Task;
import com.todoapp.model.User;
import com.todoapp.service.TaskService;
import com.todoapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    private User getAuthenticatedUser(Authentication auth) {
        return userService.findByEmail(auth.getName());
    }

    @GetMapping
    public String listTasks(Model model, Authentication auth) {
        User user = getAuthenticatedUser(auth);

        model.addAttribute("user", user);

        model.addAttribute("tasks", taskService.getTasksByUser(user));
        model.addAttribute("newTask", new Task()); 
        return "tasks";
    }

    @PostMapping
    public String createTask(@ModelAttribute("newTask") Task task, Authentication auth) {
        taskService.saveTask(task, getAuthenticatedUser(auth));
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/toggle")
    public String toggleTask(@PathVariable Long id, Authentication auth) {
        taskService.toggleTaskStatus(id, getAuthenticatedUser(auth));
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id, Authentication auth) {
        taskService.deleteTask(id, getAuthenticatedUser(auth));
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/edit")
    public String editTask(@PathVariable Long id,
                           @RequestParam String title,
                           @RequestParam String description,
                           Authentication auth) {
        taskService.updateTask(id, title, description, getAuthenticatedUser(auth));
        return "redirect:/tasks";
    }

    @PostMapping("/reorder")
    @ResponseBody
    public ResponseEntity<Void> reorderTasks(@RequestBody List<Long> taskIds, Authentication auth) {
        taskService.updateTasksOrder(taskIds, getAuthenticatedUser(auth));
        return ResponseEntity.ok().build();
    }
}