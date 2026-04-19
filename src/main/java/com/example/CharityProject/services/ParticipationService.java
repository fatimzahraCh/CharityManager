package com.example.CharityProject.services;

import com.example.CharityProject.entities.ActionCharite;
import com.example.CharityProject.entities.Participation;
import com.example.CharityProject.entities.User;
import com.example.CharityProject.repositories.ActionChariteRepository;
import com.example.CharityProject.repositories.ParticipationRepository;
import com.example.CharityProject.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    // On déclare les Repositories (la méthode de ta professeure)
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final ActionChariteRepository actionRepository;

    @Transactional
    public Participation inscrireUtilisateurAAction(Long userId, Long actionId) {

        // 1. On vérifie si l'utilisateur existe
        User participant = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable."));

        // 2. On vérifie si l'action existe et n'est pas archivée
        ActionCharite action = actionRepository.findById(actionId)
                .orElseThrow(() -> new RuntimeException("Action introuvable."));

        if (action.isArchived()) {
            throw new RuntimeException("Impossible de participer à une action archivée.");
        }

        // 3. Règle d'intégrité : On bloque les doubles participations
        if (participationRepository.existsByUserIdAndActionId(userId, actionId)) {
            throw new RuntimeException("Vous participez déjà à cette action de charité.");
        }

        // 4. On crée et on sauvegarde la participation via le Repository
        Participation nouvelleParticipation = new Participation();
        nouvelleParticipation.setDateInscription(LocalDateTime.now());
        nouvelleParticipation.setUser(participant);
        nouvelleParticipation.setAction(action);

        return participationRepository.save(nouvelleParticipation);
    }
}
