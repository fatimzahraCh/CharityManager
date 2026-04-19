package com.example.CharityProject.repositories;

import com.example.CharityProject.entities.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

    // Utilisé par le Super-Admin pour voir qui attend une validation
    List<Organisation> findByIsValidatedFalse();

    // Vérifie l'unicité du Numéro d'Identification Fiscale lors de l'inscription
    boolean existsByNif(String nif);
}