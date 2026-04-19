package com.example.CharityProject.repositories;

import com.example.CharityProject.entities.ActionCharite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionChariteRepository extends JpaRepository<ActionCharite, Long> {

    // Pour la fonctionnalité de filtre (ex: chercher toutes les actions "Santé")
    List<ActionCharite> findByCategorie(String categorie);

    // Pour n'afficher que les actions en cours (non archivées)
    List<ActionCharite> findByIsArchivedFalse();
}