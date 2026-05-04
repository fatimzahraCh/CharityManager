package com.example.CharityProject.controllers;

import com.example.CharityProject.entities.Organisation;
import com.example.CharityProject.services.ActionChariteService; // On utilise le Service
import com.example.CharityProject.services.DonService;
import com.example.CharityProject.services.OrganisationService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class WebHomeController {

    private final ActionChariteService actionService;
    private final DonService donService;
    private final OrganisationService organisationService;

    @GetMapping("/")
    public String afficherAccueilDonateurs(@RequestParam(name = "search", required = false) String search, Model model) {
        model.addAttribute("actions", actionService.rechercherActionsPubliées(search));
        model.addAttribute("search", search);
        return "index";
    }

    @GetMapping("/profile")
    public String afficherProfil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }

        boolean isOrganisation = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ORGANISATION"));
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return "redirect:/admin/dashboard";
        }

        if (isOrganisation) {
            return "redirect:/actions/dashboard";
        }

        String email = auth.getName();
        model.addAttribute("actions", actionService.enrichirActionsAvecNombreParOrganisation(actionService.obtenirActionsPubliées()));
        model.addAttribute("donations", donService.obtenirHistoriqueDonsParEmail(email));
        return "profile";
    }

    @GetMapping("/admin/dashboard")
    public String afficherAdminDashboard(Model model) {
        List<Organisation> enAttente = organisationService.obtenirOrganisationsEnAttente();
        model.addAttribute("pendingOrganisations", enAttente);
        model.addAttribute("pendingCount", enAttente.size());
        model.addAttribute("totalOrganisations", organisationService.compterOrganisations());
        return "admin/dashboard";
    }

    @PostMapping("/admin/organisations/{id}/valider")
    public String validerOrganisation(@PathVariable Long id) {
        organisationService.validerOrganisation(id);
        return "redirect:/admin/dashboard";
    }
}