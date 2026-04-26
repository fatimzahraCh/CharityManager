package com.example.CharityProject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor // Nécessaire pour @Builder
@NoArgsConstructor
public class ActionDonateurDTO {
    private Long id;
    private String titre;
    private String description;
    private String lieu;
    private String categorie;
    private List<String> mediaUrls;

    // Pour la barre de progression (essentiel pour le donateur)
    private Double objectifCollecte;
    private Double sommeActuelle;

    // On donne juste le nom de l'organisation pour la confiance
    private String organisationNom;
}