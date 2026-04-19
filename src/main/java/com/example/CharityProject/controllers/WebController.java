package com.example.CharityProject.controllers;

import com.example.CharityProject.services.ActionChariteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ActionChariteService actionService;

    @GetMapping("/")
    public String accueil(Model model) {
        // On récupère les actions depuis le service pour les envoyer à la page HTML
        model.addAttribute("actions", actionService.obtenirActionsActives());
        model.addAttribute("titrePage", "Plateforme de Charité - Casablanca");

        // Renvoie le nom du fichier HTML (sans l'extension .html)
        return "index";
    }
}