package com.example.CharityProject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder // <--- Indispensable pour le contrôleur
@AllArgsConstructor // Nécessaire pour le Builder
@NoArgsConstructor
public class OrganisationDTO {
    private Long id;
    private String nom;
    private String description;
    private String siteWeb;
    private String telephone;
}