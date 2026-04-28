package com.example.CharityProject.controllers;

import com.example.CharityProject.dto.ActionAdminDTO;
import com.example.CharityProject.dto.ActionDonateurDTO;
import com.example.CharityProject.entities.ActionCharite;
import com.example.CharityProject.entities.Organisation;
import com.example.CharityProject.repositories.OrganisationRepository;
import com.example.CharityProject.services.ActionChariteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/actions")
@RequiredArgsConstructor
public class ActionChariteController {

    private final ActionChariteService actionService;
    private final OrganisationRepository organisationRepository;

    @GetMapping("/dashboard")
    public String afficherDashboard(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // On utilise Optional pour éviter l'erreur 500 si l'entrée orga manque
        return organisationRepository.findByUserEmail(email)
                .map(orga -> {
                    List<ActionCharite> mesActions = actionService.obtenirActionsParOrganisation(orga);
                    model.addAttribute("organisation", orga);
                    model.addAttribute("actions", mesActions);
                    return "actions/dashboard";
                })
                .orElse("redirect:/login?error=no_profile");
    }

    @GetMapping("/creer")
    public String afficherFormulaire(Model model) {
        model.addAttribute("action", new ActionCharite());
        return "actions/creer";
    }

    @PostMapping("/save")
    public String enregistrerAction(@ModelAttribute("action") ActionCharite action) {
        try {
            actionService.creerActionDepuisSession(action);
            return "redirect:/actions/dashboard";
        } catch (Exception e) {
            return "redirect:/actions/creer?error=" + e.getMessage();
        }
    }

    @GetMapping("/api/liste")
    @ResponseBody
    public List<?> obtenirActionsJson() {
        List<ActionCharite> actions = actionService.obtenirToutesLesActions();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin ? actions.stream().map(this::mapToAdminDto).toList()
                : actions.stream().map(this::mapToDonateurDto).toList();
    }

    // Mappers DTO
    private ActionDonateurDTO mapToDonateurDto(ActionCharite action) {
        return ActionDonateurDTO.builder()
                .id(action.getId()).titre(action.getTitre())
                .description(action.getDescription())
                .objectifCollecte(action.getObjectifCollecte())
                .sommeActuelle(action.getSommeActuelle())
                .organisationNom(action.getOrganisation() != null ? action.getOrganisation().getNom() : "Inconnue")
                .build();
    }

    private ActionAdminDTO mapToAdminDto(ActionCharite action) {
        return ActionAdminDTO.builder()
                .id(action.getId()).titre(action.getTitre())
                .objectifCollecte(action.getObjectifCollecte())
                .organisationNom(action.getOrganisation() != null ? action.getOrganisation().getNom() : "N/A")
                .build();
    }
}