package com.todoapp.controller;

import com.todoapp.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;


    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }


    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        passwordResetService.createPasswordResetTokenForUser(email);
        model.addAttribute("message", "Si el correo existe, te hemos enviado un enlace.");
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        String result = passwordResetService.validatePasswordResetToken(token);
        if (!"valid".equals(result)) {
            return "redirect:/login?error=Token inválido o caducado";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword) {

        if (!password.equals(confirmPassword)) {
            return "redirect:/reset-password?token=" + token + "&error=Las contraseñas no coinciden";
        }

        passwordResetService.changeUserPassword(token, password);
        return "redirect:/login?success=Contraseña actualizada correctamente";
    }
}