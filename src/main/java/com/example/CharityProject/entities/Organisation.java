package com.example.CharityProject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "organisations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String adresseLegale;

    @Column(unique = true)
    private String nif; // Numéro d'identification fiscale

    private String logoUrl;

    @Column(columnDefinition = "TEXT")
    private String descriptionMissions;

    // Approbation manuelle par un super-admin pour activer le profil
    private boolean isValidated = false;

    // Lien OneToOne avec l'utilisateur qui a créé le compte de l'organisation
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Association automatique des actions créées à l'organisation [cite: 21]
    @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL)
    private List<ActionCharite> actions;
}