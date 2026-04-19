package com.example.CharityProject.repositories;


import com.example.CharityProject.entities.Don;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonRepository extends JpaRepository<Don, Long> {

    // Spring Data JPA génère la requête SQL pour trouver tous les dons d'un utilisateur
    List<Don> findByUserId(Long userId);

    // Pour trouver tous les dons effectués pour une action spécifique
    List<Don> findByActionId(Long actionId);
}
