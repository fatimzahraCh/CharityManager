package com.example.CharityProject.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ActionAdminDTO {
    private Long id;
    private String titre;
    private String description;
    private LocalDateTime dateAction;
    private String lieu;
    private String categorie;
    private Double objectifCollecte;
    private Double sommeActuelle;
    private boolean isArchived;
    private List<String> mediaUrls;

    // Infos complètes sur l'organisation pour l'audit
    private Long organisationId;
    private String organisationNom;
    private String organisationContact;
}