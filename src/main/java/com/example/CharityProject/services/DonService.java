package com.example.CharityProject.services;

import com.example.CharityProject.dto.DonRequestDTO;
import com.example.CharityProject.dto.DonResponseDTO;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DonService {

    private final DonRepository donRepository;
    private final ActionChariteRepository actionRepository;
    private final UserRepository userRepository;

    @Transactional
    public DonResponseDTO effectuerUnDon(DonRequestDTO dto, Long userId) {
        // 1. Récupérer l'utilisateur
        User donateur = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID : " + userId));

        // 2. Récupérer l'action de charité
        ActionCharite action = actionRepository.findById(dto.getActionId())
                .orElseThrow(() -> new RuntimeException("Action introuvable avec l'ID : " + dto.getActionId()));

        // 3. Créer l'entité Don
        Don nouveauDon = new Don();
        nouveauDon.setMontant(dto.getMontant());
        nouveauDon.setDateDon(LocalDateTime.now());

        // On génère un ID de transaction aléatoire pour simuler un paiement
        nouveauDon.setTransactionId(UUID.randomUUID().toString());
        nouveauDon.setStatutPaiement("SUCCESS");

        nouveauDon.setUser(donateur);
        nouveauDon.setAction(action);

        // 4. Mettre à jour la somme récoltée de l'action
        double nouvelleSomme = (action.getSommeActuelle() != null ? action.getSommeActuelle() : 0.0) + dto.getMontant();
        action.setSommeActuelle(nouvelleSomme);
        if (action.getObjectifCollecte() != null && nouvelleSomme >= action.getObjectifCollecte()) {
            action.setArchived(true);
        }
        actionRepository.save(action);

        // 5. Sauvegarder le don
        Don donSauvegarde = donRepository.save(nouveauDon);

        // 6. Retourner le DTO de réponse (Transformation)
        return mapToResponseDto(donSauvegarde);
    }

    @Transactional
    public DonResponseDTO effectuerUnDonParEmail(DonRequestDTO dto, String email) {
        if (dto.getMontant() == null || dto.getMontant() <= 0) {
            throw new RuntimeException("Le montant du don doit être supérieur à zéro.");
        }

        User donateur = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'email : " + email));

        dto.setActionId(dto.getActionId());
        return effectuerUnDon(dto, donateur.getId());
    }

    public List<Don> obtenirHistoriqueDons(Long userId) {
        return donRepository.findByUserId(userId);
    }

    public List<DonResponseDTO> obtenirHistoriqueDonsParEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> donRepository.findByUserId(user.getId()).stream()
                        .map(this::mapToResponseDto)
                        .toList())
                .orElse(List.of());
    }

    @Transactional
    public void supprimerDonsParUtilisateur(Long userId) {
        donRepository.deleteByUserId(userId);
    }

    public double obtenirTotalCollecteParAction(Long actionId) {
        Double total = donRepository.sumMontantByActionId(actionId);
        return total != null ? total : 0.0;
    }

    // Mapper interne pour transformer l'entité en DTO
    private DonResponseDTO mapToResponseDto(Don don) {
        return DonResponseDTO.builder()
                .id(don.getId())
                .montant(don.getMontant())
                .dateDon(don.getDateDon())
                .actionTitre(don.getAction() != null ? don.getAction().getTitre() : "Action inconnue")
                .donateurEmail(don.getUser() != null ? don.getUser().getEmail() : "Anonyme")
                .build();
    }
}