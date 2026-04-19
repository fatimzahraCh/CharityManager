package com.example.CharityProject.repositories;


import com.example.CharityProject.entities.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    // Récupérer toutes les participations d'un utilisateur
    List<Participation> findByUserId(Long userId);

    // Savoir qui participe à une action précise
    List<Participation> findByActionId(Long actionId);

    // Vérification de sécurité : l'utilisateur participe-t-il déjà à cette action ?
    boolean existsByUserIdAndActionId(Long userId, Long actionId);
}
