package com.example.CharityProject.services;

import com.example.CharityProject.entities.Organisation;
import com.example.CharityProject.entities.User;
import com.example.CharityProject.repositories.OrganisationRepository;
import com.example.CharityProject.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganisationService {

    // Injection des deux repositories nécessaires
    private final OrganisationRepository organisationRepository;
    private final UserRepository userRepository;

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

    // Fonctionnalité Super-Admin : Valider une organisation
    @Transactional
    public Organisation validerOrganisation(Long organisationId) {
        Organisation organisation = organisationRepository.findById(organisationId)
                .orElseThrow(() -> new RuntimeException("Organisation introuvable."));

        organisation.setValidated(true);
        return organisationRepository.save(organisation);
    }
}