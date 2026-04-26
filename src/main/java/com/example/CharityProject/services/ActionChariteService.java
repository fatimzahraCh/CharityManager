package com.example.CharityProject.services;

import com.example.CharityProject.entities.ActionCharite;
import com.example.CharityProject.entities.Organisation;
import com.example.CharityProject.repositories.ActionChariteRepository;
import com.example.CharityProject.repositories.OrganisationRepository;
import lombok.RequiredArgsConstructor;
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
    public ActionCharite creerAction(ActionCharite action, Long organisationId) {
        Organisation orga = organisationRepository.findById(organisationId)
                .orElseThrow(() -> new RuntimeException("Organisation introuvable."));

        // Sécurité métier : Une organisation non validée ne peut pas créer d'action
        if (!orga.isValidated()) {
            throw new RuntimeException("Votre organisation doit être validée par l'administrateur avant de publier une action.");
        }

        action.setOrganisation(orga);
        action.setDateAction(LocalDateTime.now());
        action.setSommeActuelle(0.0); // La cagnotte commence à 0
        action.setArchived(false);

        return actionRepository.save(action);
    }

    // Fonctionnalité d'exploration : Filtrer par catégorie (Santé, Éducation...)
    public List<ActionCharite> filtrerParCategorie(String categorie) {
        return actionRepository.findByCategorie(categorie);
    }

    public ActionCharite archiverAction(Long actionId) {
        ActionCharite action = actionRepository.findById(actionId)
                .orElseThrow(() -> new RuntimeException("Action introuvable."));
        action.setArchived(true);
        return actionRepository.save(action);
    }


    public List<ActionCharite> obtenirActionsActives() {
        return actionRepository.findByIsArchivedFalse();
    }

    public List<ActionCharite> obtenirToutesLesActions() {
        return actionRepository.findAll();
    }
}