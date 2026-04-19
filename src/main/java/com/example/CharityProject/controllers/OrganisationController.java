package com.example.CharityProject.controllers;

import com.example.CharityProject.entities.Organisation;
import com.example.CharityProject.services.OrganisationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organisations")
@RequiredArgsConstructor
public class OrganisationController {

    private final OrganisationService organisationService;

    // POST : http://localhost:8080/api/organisations/inscrire?userId=1
    @PostMapping("/inscrire")
    public ResponseEntity<?> inscrire(@RequestBody Organisation organisation, @RequestParam Long userId) { // <-- userId est récupéré depuis l'URL
        try {
            Organisation orga = organisationService.inscrireOrganisation(organisation, userId);
            return new ResponseEntity<>(orga, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // GET : http://localhost:8080/api/organisations/en-attente
    @GetMapping("/en-attente")
    public ResponseEntity<List<Organisation>> getEnAttente() {
        return ResponseEntity.ok(organisationService.obtenirOrganisationsEnAttente());
    }

    // PUT : http://localhost:8080/api/organisations/{id}/valider
    @PutMapping("/{id}/valider")
    public ResponseEntity<?> validerOrganisation(@PathVariable Long id) {
        try {
            Organisation orgaValidee = organisationService.validerOrganisation(id);
            return ResponseEntity.ok(orgaValidee);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}