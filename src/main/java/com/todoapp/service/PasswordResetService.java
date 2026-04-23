package com.todoapp.service;

import com.todoapp.model.PasswordResetToken;
import com.todoapp.model.User;
import com.todoapp.repository.PasswordResetTokenRepository;
import com.todoapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.mail.from}")
    private String fromEmail;


    public void createPasswordResetTokenForUser(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();

        PasswordResetToken myToken = new PasswordResetToken(token, user);
        tokenRepository.save(myToken);


        String resetUrl = "http://localhost:8080/reset-password?token=" + token;
        SimpleMailMessage emailMsg = new SimpleMailMessage();
        emailMsg.setFrom(fromEmail);
        emailMsg.setTo(user.getEmail());
        emailMsg.setSubject("Recupera tu contraseña - ToDo Pro");
        emailMsg.setText("Hola,\n\nHaz clic en el siguiente enlace para restablecer tu contraseña (caduca en 30 minutos):\n" + resetUrl);

        mailSender.send(emailMsg);
    }


    public String validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> passTokenOpt = tokenRepository.findByToken(token);
        if (passTokenOpt.isEmpty()) return "invalidToken";

        if (passTokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            return "expired";
        }
        return "valid";
    }


    public void changeUserPassword(String token, String newPassword) {
        PasswordResetToken passToken = tokenRepository.findByToken(token).orElseThrow();
        User user = passToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(passToken);
    }
}