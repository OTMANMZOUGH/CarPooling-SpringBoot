package org.project.carpooling.service;

import org.project.carpooling.dto.AuthResponse;
import org.project.carpooling.dto.LoginRequest;
import org.project.carpooling.dto.RegisterRequest;
import org.project.carpooling.model.User;
import org.project.carpooling.repository.UserRepository;
import org.project.carpooling.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final String DEFAULT_ROLE = "ROLE_USER";
    private static final double DEFAULT_RATING = 0.0;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        validateRegistration(request);

        User user = createUserFromRequest(request);
        userRepository.save(user);

        return new AuthResponse(
                null, // Pas de token pour l'inscription
                "Inscription réussie pour : " + user.getUsername(),
                user.getUsername()
        );
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticateUser(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwtToken = jwtService.generateToken(authentication.getName());
            String username = authentication.getName();

            return new AuthResponse(
                    jwtToken,
                    "Connexion réussie. Bienvenue, " + username + " !",
                    username
            );

        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Identifiants incorrects"
            );
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Utilisateur non trouvé"
            );
        }
    }

    private Authentication authenticateUser(LoginRequest request) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );
    }

    private void validateRegistration(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ce nom d'utilisateur est déjà utilisé"
            );
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cet email est déjà utilisé"
            );
        }
    }

    private User createUserFromRequest(RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setRating(DEFAULT_RATING);
        user.setRoles(DEFAULT_ROLE);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}