package com.example.CharityProject.services;

import com.example.CharityProject.dto.UserRegistrationDTO; // <-- Import du DTO
import com.example.CharityProject.entities.Role;
import com.example.CharityProject.entities.User;
import com.example.CharityProject.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // CHANGEMENT ICI : La méthode accepte maintenant le DTO
    public User inscrireUtilisateur(UserRegistrationDTO userDto) {
        // 1. Vérification par email via le DTO
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé par un autre compte.");
        }

        // 2. Création de l'entité User (la "vraie" table BDD)
        User user = new User();

        // 3. Mapping (Transfert des données du DTO vers l'Entité)
        user.setEmail(userDto.getEmail());
        user.setRole(Role.valueOf(userDto.getRole()));

        // 4. Hachage du mot de passe extrait du DTO
        String motDePasseHache = passwordEncoder.encode(userDto.getPassword());
        user.setPassword(motDePasseHache);

        // 5. Sauvegarde de l'entité
        return userRepository.save(user);
    }

    public User obtenirUtilisateurParId(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Introuvable"));
    }

    public List<User> obtenirTousLesUtilisateurs() {
        return userRepository.findAll();
    }
}