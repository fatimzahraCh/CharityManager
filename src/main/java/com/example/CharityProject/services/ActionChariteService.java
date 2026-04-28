package com.example.CharityProject.services;

import com.example.CharityProject.entities.ActionCharite;
import com.example.CharityProject.entities.Organisation;
import com.example.CharityProject.repositories.ActionChariteRepository;
import com.example.CharityProject.repositories.OrganisationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionChariteService {

    private final ActionChariteRepository actionRepository;
    private final OrganisationRepository organisationRepository;

    @Transactional
    public ActionCharite creerActionDepuisSession(ActionCharite action) {
        // Récupération sécurisée de l'identité connectée
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Organisation orga = organisationRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Profil Organisation introuvable pour : " + email));

        if (!orga.isValidated()) {
            throw new RuntimeException("Action refusée : Votre organisation n'est pas encore validée.");
        }

        // Hydratation automatique des champs protégés
        action.setOrganisation(orga);
        action.setDateAction(LocalDateTime.now());
        action.setSommeActuelle(0.0);
        action.setArchived(false);

        return actionRepository.save(action);
    }

    // Utilisé par le Dashboard pour n'afficher que les publications de l'orga
    public List<ActionCharite> obtenirActionsParOrganisation(Organisation orga) {
        return actionRepository.findByOrganisation(orga);
    }

    public List<ActionCharite> obtenirToutesLesActions() {
        return actionRepository.findAll();
    }
}