package com.example.CharityProject.services;

import com.example.CharityProject.dto.UserRegistrationDTO;
import com.example.CharityProject.entities.Organisation; // Import ajouté
import com.example.CharityProject.entities.Role;
import com.example.CharityProject.entities.User;
import com.example.CharityProject.repositories.OrganisationRepository; // Import ajouté
import com.example.CharityProject.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import ajouté

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository; // Injecté ici
    private final PasswordEncoder passwordEncoder;
    private final DonService donService;

    @Transactional // CRITIQUE : Assure que les deux tables sont mises à jour ou aucune
    public User inscrireUtilisateur(UserRegistrationDTO userDto) {

        // 1. Vérification de l'existence de l'email
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }

        // 2. Création et configuration de l'entité User
        User user = new User();
        user.setEmail(userDto.getEmail());
        // Assure-toi que ces champs existent dans ton DTO
        user.setLastName(userDto.getLastName());

        Role selectedRole = Role.valueOf(userDto.getRole());
        user.setRole(selectedRole);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // 3. Sauvegarde de l'User
        User savedUser = userRepository.save(user);

        // 4. LOGIQUE SPÉCIFIQUE : Si c'est une organisation, on crée son profil métier
        if (selectedRole == Role.ROLE_ORGANISATION) {
            Organisation orga = new Organisation();

            // On récupère les infos depuis le DTO (ajoute ces champs dans ton DTO s'ils manquent)
            orga.setNom(userDto.getNomOrganisation());
            orga.setNif(userDto.getNif());
            orga.setAdresseLegale(userDto.getAdresse());

            // LIEN CRUCIAL : On lie l'organisation à l'utilisateur qu'on vient de créer
            orga.setUser(savedUser);

            // Sécurité : L'organisation n'est pas validée par défaut
            orga.setValidated(false);

            organisationRepository.save(orga);
        }

        return savedUser;
    }

    public User obtenirUtilisateurParId(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    public List<User> obtenirTousLesUtilisateurs() {
        return userRepository.findAll();
    }

    public List<User> obtenirUtilisateursParRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Transactional
    public User creerDonateur(String email, String password, String lastName) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setLastName(lastName);
        user.setRole(Role.ROLE_USER);

        return userRepository.save(user);
    }

    @Transactional
    public User modifierUtilisateur(Long id, String email, String lastName, String password) {
        User user = obtenirUtilisateurParId(id);

        if (!user.getEmail().equals(email) && userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }

        user.setEmail(email);
        user.setLastName(lastName);
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        return userRepository.save(user);
    }

    @Transactional
    public void supprimerUtilisateur(Long id) {
        User user = obtenirUtilisateurParId(id);
        donService.supprimerDonsParUtilisateur(id);

        if (user.getRole() == Role.ROLE_ORGANISATION) {
            organisationRepository.findByUserEmail(user.getEmail()).ifPresent(organisationRepository::delete);
        }

        userRepository.delete(user);
    }
}
