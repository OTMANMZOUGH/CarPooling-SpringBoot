package org.project.carpooling.service;

import org.project.carpooling.dto.AuthResponse;
import org.project.carpooling.dto.LoginRequest;
import org.project.carpooling.dto.RegisterRequest;
import org.project.carpooling.model.User;
import org.project.carpooling.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------------- Register ----------------
    public AuthResponse register(RegisterRequest request) {
        // Vérification si email ou username existe déjà
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Nom d'utilisateur déjà utilisé");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // Création de l'utilisateur
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setRating((double) 0); // valeur par défaut
        user.setRoles("USER"); // rôle par défaut
        user.setPassword(passwordEncoder.encode(request.getPassword())); // encodage du mot de passe
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Sauvegarde en base
        userRepository.save(user);

        return new AuthResponse("Inscription réussie pour : " + user.getUsername());
    }

    // ---------------- Login ----------------
    public AuthResponse login(LoginRequest request) {
        // Recherche de l'utilisateur par email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Vérification du mot de passe
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        // Ici, si tu veux utiliser JWT, c'est à cet endroit que tu le génères
        return new AuthResponse("Connexion réussie pour : " + user.getUsername());
    }
}
