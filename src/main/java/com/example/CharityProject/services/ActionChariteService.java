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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if (action.getId() != null) {
            ActionCharite existante = actionRepository.findById(action.getId())
                    .orElseThrow(() -> new RuntimeException("Action introuvable pour édition : " + action.getId()));
            if (!existante.getOrganisation().getId().equals(orga.getId())) {
                throw new RuntimeException("Vous ne pouvez modifier qu'une action de votre organisation.");
            }
            existante.setTitre(action.getTitre());
            existante.setDescription(action.getDescription());
            existante.setObjectifCollecte(action.getObjectifCollecte());
            existante.setLieu(action.getLieu());
            existante.setCategorie(action.getCategorie());
            if (action.getImageUrl() != null && !action.getImageUrl().isBlank()) {
                existante.setImageUrl(action.getImageUrl());
            }
            return actionRepository.save(existante);
        }

        // Hydratation automatique des champs protégés pour la création
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

    public List<ActionCharite> obtenirActionsPubliées() {
        return actionRepository.findByIsArchivedFalse();
    }

    public List<ActionCharite> rechercherActionsPubliées(String motCle) {
        if (motCle == null || motCle.isBlank()) {
            return obtenirActionsPubliées();
        }
        return actionRepository.searchPublishedByKeyword(motCle.trim());
    }

    public List<ActionCharite> enrichirActionsAvecNombreParOrganisation(List<ActionCharite> actions) {
        Map<Long, Integer> organisationCounts = new HashMap<>();
        for (ActionCharite action : actions) {
            if (action.getOrganisation() != null && action.getOrganisation().getId() != null) {
                Long orgId = action.getOrganisation().getId();
                organisationCounts.put(orgId, organisationCounts.getOrDefault(orgId, 0) + 1);
            }
        }
        for (ActionCharite action : actions) {
            if (action.getOrganisation() != null && action.getOrganisation().getId() != null) {
                action.setPublishedActionsCountByOrganisation(organisationCounts.get(action.getOrganisation().getId()));
            } else {
                action.setPublishedActionsCountByOrganisation(0);
            }
        }
        return actions;
    }

    public ActionCharite obtenirActionParId(Long id) {
        return actionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action introuvable avec l'ID : " + id));
    }

    @Transactional
    public void supprimerAction(Long id) {
        ActionCharite action = obtenirActionParId(id);
        actionRepository.delete(action);
    }
} 