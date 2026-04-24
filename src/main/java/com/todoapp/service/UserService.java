package com.todoapp.service;

import com.todoapp.dto.UserRegistrationDto;
import com.todoapp.model.User;
import com.todoapp.repository.PasswordResetTokenRepository;
import com.todoapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository; // NUEVO

    public User save(UserRegistrationDto registrationDto) {
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new RuntimeException("Las contraseñas no coinciden.");
        }
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new RuntimeException("El correo electrónico ya está registrado.");
        }
        if (userRepository.findByUsername(registrationDto.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya está en uso. Elige otro.");
        }

        User user = User.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .role("USER")
                .build();

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

   

    @Transactional
    public void updateUsername(User user, String newUsername) {
        if (!user.getUsername().equals(newUsername)) {
            if (userRepository.findByUsername(newUsername).isPresent()) {
                throw new RuntimeException("El nombre de usuario ya está en uso.");
            }
            user.setUsername(newUsername);
            userRepository.save(user);
        }
    }

    @Transactional
    public void changePassword(User user, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(User user) {
        tokenRepository.deleteByUser(user);
        userRepository.delete(user);
    }
}