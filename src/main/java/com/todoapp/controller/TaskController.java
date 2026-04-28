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
import java.time.LocalDate;
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
    public String listTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String search, // NUEVO BUSCADOR
            Model model, Authentication auth) {

        User user = getAuthenticatedUser(auth);
        model.addAttribute("user", user);

        model.addAttribute("tasks", taskService.getFilteredAndSortedTasks(user, status, category, sort, search));
        model.addAttribute("newTask", new Task());
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentCategory", category);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentSearch", search); // Guardamos la búsqueda actual

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
                           @RequestParam(required = false) String category,
                           @RequestParam(required = false) LocalDate dueDate,
                           @RequestParam(required = false) String tags,
                           Authentication auth) {
        taskService.updateTask(id, title, description, category, dueDate, tags, getAuthenticatedUser(auth));
        return "redirect:/tasks";
    }

    @PostMapping("/reorder")
    @ResponseBody
    public ResponseEntity<Void> reorderTasks(@RequestBody List<Long> taskIds, Authentication auth) {
        taskService.updateTasksOrder(taskIds, getAuthenticatedUser(auth));
        return ResponseEntity.ok().build();
    }


    @GetMapping("/export")
    public void exportTasksToCSV(jakarta.servlet.http.HttpServletResponse response, Authentication auth) throws Exception {
        User user = getAuthenticatedUser(auth);
        List<Task> tasks = taskService.getFilteredAndSortedTasks(user, null, null, null, null);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"mis_tareas_todopro.csv\"");
        response.setCharacterEncoding("UTF-8");

        java.io.PrintWriter writer = response.getWriter();
        writer.print('\uFEFF');
        writer.println("ID,Título,Descripción,Estado,Categoría,Tags,Fecha Creación,Fecha Límite");

        for (Task task : tasks) {
            String desc = task.getDescription() != null ? task.getDescription().replace("\"", "\"\"") : "";
            String cat = task.getCategory() != null ? task.getCategory() : "Sin etiqueta";
            String tagsStr = task.getTags() != null ? task.getTags().replace("\"", "\"\"") : "";
            String due = task.getDueDate() != null ? task.getDueDate().toString() : "Sin fecha";
            String status = task.isCompleted() ? "Completada" : "Pendiente";

            writer.printf("%d,\"%s\",\"%s\",%s,%s,\"%s\",%s,%s\n",
                    task.getId(), task.getTitle().replace("\"", "\"\""), desc, status, cat, tagsStr, task.getCreatedAt().toLocalDate(), due);
        }
    }
}