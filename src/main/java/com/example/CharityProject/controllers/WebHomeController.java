package com.example.CharityProject.controllers;

import com.example.CharityProject.services.ActionChariteService; // On utilise le Service
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WebHomeController {

    // On injecte le Service au lieu du Repository directement
    private final ActionChariteService actionService;

    @GetMapping("/")
    public String afficherAccueilDonateurs(Model model) {
        // On récupère les actions via le service (qui gère déjà le filtrage ou les DTOs)
        model.addAttribute("actions", actionService.obtenirToutesLesActions());

        // On retourne la vue index.html
        return "index";
    }

    @GetMapping("/explore")
    public String afficherExplore(Model model) {
        model.addAttribute("actions", actionService.obtenirToutesLesActions());
        return "explore";
    }

    @GetMapping("/admin/dashboard")
    public String afficherAdminDashboard() {
        return "admin/dashboard";
    }
}