package com.example.CharityProject.repositories;

import com.example.CharityProject.entities.ActionCharite;
import com.example.CharityProject.entities.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActionChariteRepository extends JpaRepository<ActionCharite, Long> {
    // Cette méthode permet de récupérer toutes les actions d'une organisation précise
    List<ActionCharite> findByOrganisation(Organisation organisation);

    List<ActionCharite> findByIsArchivedFalse();
    List<ActionCharite> findByCategorie(String categorie);

    @Query("SELECT a FROM ActionCharite a WHERE a.isArchived = false AND (" +
            "LOWER(a.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.categorie) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.organisation.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))) ")
    List<ActionCharite> searchPublishedByKeyword(@Param("keyword") String keyword);
}