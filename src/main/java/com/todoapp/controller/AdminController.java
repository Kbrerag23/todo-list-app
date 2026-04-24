package com.todoapp.controller;

import com.todoapp.model.User;
import com.todoapp.repository.UserRepository;
import com.todoapp.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @GetMapping("/users")
    public String listAllUsers(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<User> userPage = userRepository.findAll(PageRequest.of(page, 10));

        model.addAttribute("userPage", userPage);
        model.addAttribute("totalUsers", userRepository.count()); // Estadística 1
        model.addAttribute("tasksToday", taskRepository.countTasksCreatedToday()); // Estadística 2

        return "admin-users";
    }

    @GetMapping("/users/{id}/tasks")
    public String viewUserTasks(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("targetUser", user);
        model.addAttribute("tasks", taskRepository.findByUserId(id));
        return "admin-user-tasks";
    }
}