package com.example.CharityProject.services;


import com.example.CharityProject.entities.ActionCharite;
import com.example.CharityProject.entities.Don;
import com.example.CharityProject.entities.User;
import com.example.CharityProject.repositories.ActionChariteRepository;
import com.example.CharityProject.repositories.DonRepository;
import com.example.CharityProject.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor // Injecte automatiquement tes Repositories
public class DonService {

    // 1. Déclaration des Repositories (comme le fait ta prof)
    private final DonRepository donRepository;
    private final ActionChariteRepository actionRepository;
    private final UserRepository userRepository;

    // 2. Appel dans la méthode métier
    @Transactional // Garantit que si une erreur survient, rien n'est sauvegardé en base
    public Don effectuerUnDon(Long userId, Long actionId, Double montant, String transactionId) {

        // A. Récupérer l'utilisateur et l'action depuis la base
        User donateur = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        ActionCharite action = actionRepository.findById(actionId)
                .orElseThrow(() -> new RuntimeException("Action de charité introuvable"));

        // B. Créer le don
        Don nouveauDon = new Don();
        nouveauDon.setMontant(montant);
        nouveauDon.setDateDon(LocalDateTime.now());
        nouveauDon.setTransactionId(transactionId);
        nouveauDon.setStatutPaiement("SUCCESS");
        nouveauDon.setUser(donateur);
        nouveauDon.setAction(action);

        // C. Mettre à jour la somme récoltée de l'action
        action.setSommeActuelle(action.getSommeActuelle() + montant);
        actionRepository.save(action); // On sauvegarde la progression

        // D. Sauvegarder le don via le Repository
        return donRepository.save(nouveauDon);
    }

    // Méthode pour l'historique des dons (Cahier des charges)
    public List<Don> obtenirHistoriqueDons(Long userId) {
        return donRepository.findByUserId(userId);
    }
}