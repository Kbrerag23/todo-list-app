package com.todoapp.service;

import com.todoapp.model.PasswordResetToken;
import com.todoapp.model.User;
import com.todoapp.repository.PasswordResetTokenRepository;
import com.todoapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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

        try {
            // Creamos el mensaje con soporte HTML
            MimeMessage message = mailSender.createMimeMessage();
            // El 'true' indica que es un correo multipart (soporta HTML)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Recupera tu contraseña - ToDo Pro");

            // Diseño HTML del correo con el botón
            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 15px; text-align: center;'>"
                    + "<h2 style='color: #1e293b; margin-bottom: 5px;'>🔐 ToDo Pro</h2>"
                    + "<h3 style='color: #334155;'>Recuperación de contraseña</h3>"
                    + "<p style='color: #64748b; font-size: 16px; line-height: 1.5;'>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta. Haz clic en el botón de abajo para elegir una nueva.</p>"
                    + "<br>"
                    + "<a href='" + resetUrl + "' style='display: inline-block; padding: 14px 30px; background-color: #6366f1; color: #ffffff; text-decoration: none; border-radius: 50px; font-weight: bold; font-size: 16px; box-shadow: 0 4px 6px rgba(99, 102, 241, 0.2);'>Restablecer mi contraseña</a>"
                    + "<br><br>"
                    + "<p style='color: #94a3b8; font-size: 14px; margin-top: 20px;'>Si no has sido tú, puedes ignorar este correo. El enlace caducará en 30 minutos.</p>"
                    + "<hr style='border: none; border-top: 1px solid #e2e8f0; margin: 20px 0;'>"
                    + "<p style='color: #cbd5e1; font-size: 12px; word-break: break-all;'>Si el botón no funciona, copia y pega este enlace en tu navegador:<br><br>" + resetUrl + "</p>"
                    + "</div>";

            // Pasamos el contenido y confirmamos que es HTML (true)
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            // Si algo falla al construir el correo HTML, mostramos el error en la consola
            System.err.println("Error al enviar el correo HTML de recuperación: " + e.getMessage());
        }
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