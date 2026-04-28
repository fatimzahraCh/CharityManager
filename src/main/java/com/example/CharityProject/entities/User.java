package com.example.CharityProject.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users") // CRITIQUE POUR POSTGRESQL : "user" est un mot réservé
@Data // Génère Getters, Setters, toString, etc.
@NoArgsConstructor // Constructeur vide requis par JPA
@AllArgsConstructor

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;

    // L'annotation @Enumerated(EnumType.STRING) stocke le texte "ROLE_USER" en BDD
    // au lieu d'un simple chiffre (0, 1, 2), ce qui est beaucoup plus lisible.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Relation : Un utilisateur peut faire plusieurs dons

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Don> dons;
}