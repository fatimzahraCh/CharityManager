package com.example.CharityProject.services;

import com.example.CharityProject.entities.Organisation;
import com.example.CharityProject.entities.Role;
import com.example.CharityProject.entities.User;
import com.example.CharityProject.repositories.OrganisationRepository;
import com.example.CharityProject.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganisationService {

    // Injection des deux repositories nécessaires
    private final OrganisationRepository organisationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Ajout du paramètre Long userId pour lier l'organisation à son créateur
    public Organisation inscrireOrganisation(Organisation organisation, Long userId) {
        if (organisationRepository.existsByNif(organisation.getNif())) {
            throw new RuntimeException("Une organisation avec ce Numéro d'Identification Fiscale existe déjà.");
        }

        // 1. Récupérer l'utilisateur depuis la base de données
        User createur = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable."));

        // 2. Associer cet utilisateur à l'organisation pour satisfaire la contrainte "not-null" de PostgreSQL
        organisation.setUser(createur);

        // 3. Par défaut, l'organisation n'est pas validée lors de l'inscription
        organisation.setValidated(false);

        return organisationRepository.save(organisation);
    }

    // Fonctionnalité Super-Admin : Lister les demandes en attente
    public List<Organisation> obtenirOrganisationsEnAttente() {
        return organisationRepository.findByIsValidatedFalse();
    }

    // Fonctionnalité Super-Admin : Nombre total d'organisations
    public long compterOrganisations() {
        return organisationRepository.count();
    }

    // Fonctionnalité Super-Admin : Valider une organisation
    @Transactional
    public Organisation validerOrganisation(Long organisationId) {
        Organisation organisation = organisationRepository.findById(organisationId)
                .orElseThrow(() -> new RuntimeException("Organisation introuvable."));

        organisation.setValidated(true);
        return organisationRepository.save(organisation);
    }

    public List<Organisation> obtenirToutesLesOrganisations() {
        return organisationRepository.findAll();
    }

    public Organisation obtenirOrganisationParId(Long id) {
        return organisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisation introuvable."));
    }

    @Transactional
    public Organisation creerOrganisationAdmin(String email, String password, String lastName,
                                                String nom, String nif, String adresseLegale,
                                                String logoUrl, String descriptionMissions,
                                                boolean validated) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }
        if (organisationRepository.existsByNif(nif)) {
            throw new RuntimeException("Une organisation avec ce NIF existe déjà.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setLastName(lastName);
        user.setRole(Role.ROLE_ORGANISATION);
        user = userRepository.save(user);

        Organisation organisation = new Organisation();
        organisation.setNom(nom);
        organisation.setNif(nif);
        organisation.setAdresseLegale(adresseLegale);
        organisation.setLogoUrl(logoUrl);
        organisation.setDescriptionMissions(descriptionMissions);
        organisation.setValidated(validated);
        organisation.setUser(user);

        return organisationRepository.save(organisation);
    }

    @Transactional
    public Organisation modifierOrganisationAdmin(Long id, String email, String password, String lastName,
                                                   String nom, String nif, String adresseLegale,
                                                   String logoUrl, String descriptionMissions,
                                                   boolean validated) {
        Organisation organisation = obtenirOrganisationParId(id);
        User user = organisation.getUser();

        if (!user.getEmail().equals(email) && userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }

        user.setEmail(email);
        user.setLastName(lastName);
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        userRepository.save(user);

        organisation.setNom(nom);
        organisation.setNif(nif);
        organisation.setAdresseLegale(adresseLegale);
        organisation.setLogoUrl(logoUrl);
        organisation.setDescriptionMissions(descriptionMissions);
        organisation.setValidated(validated);

        return organisationRepository.save(organisation);
    }

    @Transactional
    public void supprimerOrganisation(Long id) {
        Organisation organisation = obtenirOrganisationParId(id);
        User user = organisation.getUser();
        organisationRepository.delete(organisation);
        if (user != null) {
            userRepository.delete(user);
        }
    }
}