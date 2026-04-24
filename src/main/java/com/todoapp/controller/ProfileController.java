package com.todoapp.controller;

import com.todoapp.model.User;
import com.todoapp.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    private User getAuthenticatedUser(Authentication auth) {
        return userService.findByEmail(auth.getName());
    }

    @GetMapping
    public String viewProfile(Model model, Authentication auth) {
        model.addAttribute("user", getAuthenticatedUser(auth));
        return "profile";
    }

    @PostMapping("/update-username")
    public String updateUsername(@RequestParam String username, Authentication auth) {
        try {
            userService.updateUsername(getAuthenticatedUser(auth), username);
            return "redirect:/profile?success=Nombre actualizado correctamente";
        } catch (Exception e) {
            return "redirect:/profile?error=" + e.getMessage();
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, Authentication auth) {
        try {
            userService.changePassword(getAuthenticatedUser(auth), oldPassword, newPassword);
            return "redirect:/profile?success=Contraseña actualizada con éxito";
        } catch (Exception e) {
            return "redirect:/profile?error=" + e.getMessage();
        }
    }

    @PostMapping("/delete-account")
    public String deleteAccount(Authentication auth, HttpServletRequest request) throws ServletException {
        userService.deleteAccount(getAuthenticatedUser(auth));
        request.logout(); 
        return "redirect:/login?success=Cuenta eliminada definitivamente. ¡Hasta pronto!";
    }
}