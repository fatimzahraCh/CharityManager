package com.example.CharityProject.controllers;

import com.example.CharityProject.dto.DonRequestDTO;
import com.example.CharityProject.dto.DonResponseDTO;
import com.example.CharityProject.entities.Don;
import com.example.CharityProject.services.DonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dons")
@RequiredArgsConstructor
public class DonController {

    private final DonService donService;

    /**
     * Effectue un nouveau don.
     * URL : POST http://localhost:8080/api/dons/effectuer?userId=1
     * Body JSON : { "montant": 500.0, "actionId": 12 }
     */
    @PostMapping("/effectuer")
    public ResponseEntity<?> effectuerDon(@RequestBody DonRequestDTO dto, @RequestParam Long userId) {
        try {
            // Le service s'occupe de la logique et nous rend un DTO de réponse
            DonResponseDTO response = donService.effectuerUnDon(dto, userId);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // En cas d'erreur (montant négatif, action introuvable, etc.)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Récupère l'historique des dons d'un utilisateur sous forme de DTOs.
     * URL : GET http://localhost:8080/api/dons/historique/1
     */
    @GetMapping("/historique/{userId}")
    public ResponseEntity<List<DonResponseDTO>> historiqueDons(@PathVariable Long userId) {
        List<Don> dons = donService.obtenirHistoriqueDons(userId);

        // Conversion de la liste d'entités en liste de DTOs pour un affichage propre
        List<DonResponseDTO> responseDtos = dons.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    // --- MÉTHODE DE MAPPING INTERNE ---
    // Cette méthode transforme l'entité Don (complexe) en DonResponseDTO (simple)
    private DonResponseDTO mapToResponseDto(Don don) {
        return DonResponseDTO.builder()
                .id(don.getId())
                .montant(don.getMontant())
                .dateDon(don.getDateDon())
                // On récupère uniquement le titre de l'action et l'email de l'utilisateur
                .actionTitre(don.getAction() != null ? don.getAction().getTitre() : "Action inconnue")
                .donateurEmail(don.getUser() != null ? don.getUser().getEmail() : "Anonyme")
                .build();
    }
}