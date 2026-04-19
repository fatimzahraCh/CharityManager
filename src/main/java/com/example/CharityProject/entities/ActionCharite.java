package com.example.CharityProject.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "actions_charite")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionCharite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime dateAction;
    private String lieu;

    @Column(nullable = false)
    private Double objectifCollecte;

    private Double sommeActuelle = 0.0;

    private String categorie;

    private boolean isArchived = false;

    // Astuce PostgreSQL : @ElementCollection permet de stocker une liste de chaînes
    // de caractères (ici les URLs des médias) sans créer une entité complexe[cite: 23].
    @ElementCollection
    @CollectionTable(name = "action_medias", joinColumns = @JoinColumn(name = "action_id"))
    @Column(name = "media_url")
    private List<String> mediaUrls;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL)
    private List<Don> dons;
}