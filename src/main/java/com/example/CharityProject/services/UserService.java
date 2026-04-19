package com.example.CharityProject.services;

import com.example.CharityProject.entities.User;
import com.example.CharityProject.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Inscription avec vérification d'email
    public User inscrireUtilisateur(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé par un autre compte.");
        }
        // Ici, plus tard, on ajoutera le hachage du mot de passe avec BCrypt (Spring Security)
        return userRepository.save(user);
    }

    public User obtenirUtilisateurParId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable."));
    }

    public List<User> obtenirTousLesUtilisateurs() {
        return userRepository.findAll();
    }
}