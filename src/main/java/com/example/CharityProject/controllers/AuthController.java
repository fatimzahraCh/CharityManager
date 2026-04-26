package com.example.CharityProject.controllers;

import com.example.CharityProject.dto.UserRegistrationDTO; // Import du DTO
import com.example.CharityProject.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String afficherPageConnexion() {
        return "login";
    }

    @GetMapping("/register")
    public String afficherPageInscription(Model model) {
        // On envoie le DTO vide au lieu de l'entité
        model.addAttribute("userDto", new UserRegistrationDTO());
        return "register";
    }

    @PostMapping("/register")
    public String traiterInscription(@ModelAttribute("userDto") UserRegistrationDTO userDto) {
        // On passe le DTO au service
        userService.inscrireUtilisateur(userDto);

        return "redirect:/login?registered";
    }
}