package com.example.CharityProject.controllers;

import com.example.CharityProject.dto.ActionAdminDTO;
import com.example.CharityProject.dto.ActionDonateurDTO;
import com.example.CharityProject.entities.ActionCharite;
import com.example.CharityProject.services.ActionChariteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actions")
@RequiredArgsConstructor
public class ActionChariteController {

    private final ActionChariteService actionService;

    @PostMapping("/creer")
    public ResponseEntity<?> creerAction(@RequestBody ActionCharite action, @RequestParam Long organisationId) {
        try {
            ActionCharite nouvelleAction = actionService.creerAction(action, organisationId);
            return new ResponseEntity<>(mapToAdminDto(nouvelleAction), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de la création : " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/liste")
    public ResponseEntity<?> obtenirActions() {
        List<ActionCharite> actions = actionService.obtenirToutesLesActions();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.ok(actions.stream().map(this::mapToAdminDto).toList());
        } else {
            return ResponseEntity.ok(actions.stream().map(this::mapToDonateurDto).toList());
        }
    }

    // --- MAPPERS AVEC PROTECTION CONTRE LES NULL ---

    private ActionDonateurDTO mapToDonateurDto(ActionCharite action) {
        return ActionDonateurDTO.builder()
                .id(action.getId())
                .titre(action.getTitre())
                .description(action.getDescription())
                .lieu(action.getLieu())
                .categorie(action.getCategorie())
                .mediaUrls(action.getMediaUrls())
                .objectifCollecte(action.getObjectifCollecte())
                .sommeActuelle(action.getSommeActuelle())
                // Protection : si pas d'organisation, on met "Anonyme"
                .organisationNom(action.getOrganisation() != null ? action.getOrganisation().getNom() : "Organisation inconnue")
                .build();
    }

    private ActionAdminDTO mapToAdminDto(ActionCharite action) {
        return ActionAdminDTO.builder()
                .id(action.getId())
                .titre(action.getTitre())
                .description(action.getDescription())
                .dateAction(action.getDateAction())
                .lieu(action.getLieu())
                .categorie(action.getCategorie())
                .objectifCollecte(action.getObjectifCollecte())
                .sommeActuelle(action.getSommeActuelle())
                .isArchived(action.isArchived())
                .organisationId(action.getOrganisation() != null ? action.getOrganisation().getId() : null)
                .organisationNom(action.getOrganisation() != null ? action.getOrganisation().getNom() : "N/A")
                .build();
    }
}