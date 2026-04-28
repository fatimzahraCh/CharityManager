package com.example.CharityProject.dto;

import lombok.Data;

@Data
public class UserRegistrationDTO {
    // Infos de connexion
    private String email;
    private String password;
    private String role;

    // Infos personnelles (On garde uniquement le nom)
    private String lastName;

    // Infos spécifiques à l'Organisation
    private String nomOrganisation;
    private String nif;
    private String adresse;
}