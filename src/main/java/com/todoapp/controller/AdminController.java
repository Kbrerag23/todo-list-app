package com.todoapp.controller;

import com.todoapp.model.Category;
import com.todoapp.model.User;
import com.todoapp.repository.CategoryRepository;
import com.todoapp.repository.TaskRepository;
import com.todoapp.repository.UserRepository;
import com.todoapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @GetMapping("/users")
    public String listAllUsers(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<User> userPage = userRepository.findAll(PageRequest.of(page, 10));

        model.addAttribute("userPage", userPage);
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("tasksToday", taskRepository.countTasksCreatedToday());

        return "admin-users";
    }

    @GetMapping("/users/{id}/tasks")
    public String viewUserTasks(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Map<String, String> categoryColors = new HashMap<>();
        for (Category cat : categoryRepository.findAll()) {
            categoryColors.put(cat.getName(), cat.getColor() != null ? cat.getColor() : "#6c757d");
        }

        model.addAttribute("targetUser", user);
        model.addAttribute("tasks", taskRepository.findByUserId(id));
        model.addAttribute("categoryColors", categoryColors);

        return "admin-user-tasks";
    }

    @PostMapping("/users/{id}/promote")
    public String promoteUser(@PathVariable Long id) {
        userService.promoteToAdmin(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin-categories";
    }

    @PostMapping("/categories")
    public String createCategory(@RequestParam String name, @RequestParam(defaultValue = "#6366f1") String color) {
        if (name != null && !name.trim().isEmpty()) {
            Category category = new Category();
            category.setName(name.trim());
            category.setColor(color);
            categoryRepository.save(category);
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/admin/categories";
    }
}