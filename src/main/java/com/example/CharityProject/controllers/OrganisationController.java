package com.example.CharityProject.controllers;

import com.example.CharityProject.dto.OrganisationDTO;
import com.example.CharityProject.entities.Organisation;
import com.example.CharityProject.services.OrganisationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors; // Ajout de l'import

@RestController
@RequestMapping("/api/organisations")
@RequiredArgsConstructor
public class OrganisationController {

    private final OrganisationService organisationService;

    @PostMapping("/inscrire")
    public ResponseEntity<?> inscrire(@RequestBody Organisation organisation, @RequestParam Long userId) {
        try {
            Organisation orga = organisationService.inscrireOrganisation(organisation, userId);
            return new ResponseEntity<>(mapToDto(orga), HttpStatus.CREATED);
        } catch (Exception e) { // Utilise Exception pour capturer toutes les erreurs
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/en-attente")
    public ResponseEntity<List<OrganisationDTO>> getEnAttente() {
        List<Organisation> enAttente = organisationService.obtenirOrganisationsEnAttente();
        // Version plus compatible pour transformer en liste
        List<OrganisationDTO> dtos = enAttente.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/valider")
    public ResponseEntity<?> validerOrganisation(@PathVariable Long id) {
        try {
            Organisation orgaValidee = organisationService.validerOrganisation(id);
            return ResponseEntity.ok(mapToDto(orgaValidee));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    private OrganisationDTO mapToDto(Organisation organisation) {
        return OrganisationDTO.builder()
                .id(organisation.getId())
                .nom(organisation.getNom())
                .description(organisation.getDescriptionMissions())
                .siteWeb(organisation.getLogoUrl())
                .telephone(organisation.getAdresseLegale())
                .build();
    }
}