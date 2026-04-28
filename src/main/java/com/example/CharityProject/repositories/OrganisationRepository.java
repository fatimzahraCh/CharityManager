package com.example.CharityProject.repositories;

import com.example.CharityProject.entities.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

    // On traverse la relation vers l'entité User pour trouver l'email
    Optional<Organisation> findByUserEmail(String email);

    // Pour la gestion administrative
    List<Organisation> findByIsValidatedFalse();

    // Pour l'inscription unique
    boolean existsByNif(String nif);
}