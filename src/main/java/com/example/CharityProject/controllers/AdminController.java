package com.example.CharityProject.controllers;

import com.example.CharityProject.entities.Organisation;
import com.example.CharityProject.entities.Role;
import com.example.CharityProject.entities.User;
import com.example.CharityProject.services.OrganisationService;
import com.example.CharityProject.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final OrganisationService organisationService;

    @GetMapping("/manage")
    public String afficherAdminGestion(Model model,
                                       @RequestParam(value = "message", required = false) String message) {
        model.addAttribute("organisations", organisationService.obtenirToutesLesOrganisations());
        model.addAttribute("donateurs", userService.obtenirUtilisateursParRole(Role.ROLE_USER));
        model.addAttribute("message", message);
        return "admin/manage";
    }

    @PostMapping("/users/create")
    public String creerDonateur(@RequestParam String email,
                                @RequestParam String lastName,
                                @RequestParam String password,
                                RedirectAttributes redirectAttributes) {
        userService.creerDonateur(email, password, lastName);
        redirectAttributes.addAttribute("message", "Donateur créé avec succès.");
        return "redirect:/admin/manage";
    }

    @GetMapping("/users/{id}/edit")
    public String afficherFormulaireEditionDonateur(@PathVariable Long id, Model model) {
        User user = userService.obtenirUtilisateurParId(id);
        model.addAttribute("user", user);
        return "admin/user_form";
    }

    @PostMapping("/users/{id}/edit")
    public String modifierDonateur(@PathVariable Long id,
                                   @RequestParam String email,
                                   @RequestParam String lastName,
                                   @RequestParam(required = false) String password,
                                   RedirectAttributes redirectAttributes) {
        userService.modifierUtilisateur(id, email, lastName, password);
        redirectAttributes.addAttribute("message", "Donateur modifié avec succès.");
        return "redirect:/admin/manage";
    }

    @PostMapping("/users/{id}/delete")
    public String supprimerDonateur(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.supprimerUtilisateur(id);
        redirectAttributes.addAttribute("message", "Donateur supprimé avec succès.");
        return "redirect:/admin/manage";
    }

    @PostMapping("/organisations/create")
    public String creerOrganisation(@RequestParam String email,
                                    @RequestParam String lastName,
                                    @RequestParam String password,
                                    @RequestParam String nom,
                                    @RequestParam String nif,
                                    @RequestParam String adresseLegale,
                                    @RequestParam(required = false) String logoUrl,
                                    @RequestParam(required = false) String descriptionMissions,
                                    @RequestParam(required = false, defaultValue = "false") boolean validated,
                                    RedirectAttributes redirectAttributes) {
        organisationService.creerOrganisationAdmin(email, password, lastName, nom, nif, adresseLegale, logoUrl, descriptionMissions, validated);
        redirectAttributes.addAttribute("message", "Organisation créée avec succès.");
        return "redirect:/admin/manage";
    }

    @GetMapping("/organisations/{id}/edit")
    public String afficherFormulaireEditionOrganisation(@PathVariable Long id, Model model) {
        Organisation organisation = organisationService.obtenirOrganisationParId(id);
        model.addAttribute("organisation", organisation);
        return "admin/organisation_form";
    }

    @PostMapping("/organisations/{id}/edit")
    public String modifierOrganisation(@PathVariable Long id,
                                       @RequestParam String email,
                                       @RequestParam String lastName,
                                       @RequestParam(required = false) String password,
                                       @RequestParam String nom,
                                       @RequestParam String nif,
                                       @RequestParam String adresseLegale,
                                       @RequestParam(required = false) String logoUrl,
                                       @RequestParam(required = false) String descriptionMissions,
                                       @RequestParam(required = false, defaultValue = "false") boolean validated,
                                       RedirectAttributes redirectAttributes) {
        organisationService.modifierOrganisationAdmin(id, email, password, lastName, nom, nif, adresseLegale, logoUrl, descriptionMissions, validated);
        redirectAttributes.addAttribute("message", "Organisation modifiée avec succès.");
        return "redirect:/admin/manage";
    }

    @PostMapping("/organisations/{id}/delete")
    public String supprimerOrganisation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        organisationService.supprimerOrganisation(id);
        redirectAttributes.addAttribute("message", "Organisation supprimée avec succès.");
        return "redirect:/admin/manage";
    }
}
