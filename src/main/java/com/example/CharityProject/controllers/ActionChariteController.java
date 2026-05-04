package com.example.CharityProject.controllers;

import com.example.CharityProject.dto.ActionAdminDTO;
import com.example.CharityProject.dto.ActionDonateurDTO;
import com.example.CharityProject.entities.ActionCharite;
import com.example.CharityProject.entities.Organisation;
import com.example.CharityProject.dto.DonRequestDTO;
import com.example.CharityProject.repositories.OrganisationRepository;
import com.example.CharityProject.services.ActionChariteService;
import com.example.CharityProject.services.DonService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/actions")
@RequiredArgsConstructor
public class ActionChariteController {

    private final ActionChariteService actionService;
    private final DonService donService;
    private final OrganisationRepository organisationRepository;

    @GetMapping("/dashboard")
    public String afficherDashboard(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // On utilise Optional pour éviter l'erreur 500 si l'entrée orga manque
        return organisationRepository.findByUserEmail(email)
                .map(orga -> {
                    List<ActionCharite> mesActions = actionService.obtenirActionsParOrganisation(orga);
                    mesActions.forEach(action -> action.setSommeActuelle(donService.obtenirTotalCollecteParAction(action.getId())));
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

    @GetMapping("/editer/{id}")
    public String afficherFormulaireEdition(@PathVariable Long id, Model model) {
        ActionCharite action = actionService.obtenirActionParId(id);
        model.addAttribute("action", action);
        return "actions/creer";
    }

    @PostMapping("/save")
    public String enregistrerAction(@ModelAttribute("action") ActionCharite action,
                                    @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = enregistrerImage(imageFile);
                action.setImageUrl("/uploads/" + fileName);
            }
            actionService.creerActionDepuisSession(action);
            return "redirect:/actions/dashboard";
        } catch (Exception e) {
            return action.getId() != null ? "redirect:/actions/editer/" + action.getId() + "?error=" + e.getMessage()
                    : "redirect:/actions/creer?error=" + e.getMessage();
        }
    }

    private String enregistrerImage(MultipartFile imageFile) throws IOException {
        String uploadDir = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "uploads").toString();
        Path uploadPath = Paths.get(uploadDir);
        if (Files.notExists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = imageFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String fileName = System.currentTimeMillis() + extension;
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    @GetMapping("/detail/{id}")
    public String afficherDetailAction(@PathVariable Long id, Model model, @RequestParam(required = false) String message) {
        ActionCharite action = actionService.obtenirActionParId(id);
        action.setSommeActuelle(donService.obtenirTotalCollecteParAction(action.getId()));

        // Vérifier si l'utilisateur connecté est propriétaire de l'action
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isOwner = false;
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String email = auth.getName();
            isOwner = action.getOrganisation() != null &&
                     action.getOrganisation().getUser() != null &&
                     email.equals(action.getOrganisation().getUser().getEmail());
        }

        model.addAttribute("action", action);
        model.addAttribute("donRequest", new DonRequestDTO());
        model.addAttribute("message", message);
        model.addAttribute("isOwner", isOwner);
        return "action_details";
    }

    @PostMapping("/donner")
    public String effectuerDon(@ModelAttribute("donRequest") DonRequestDTO donRequest,
                               RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }

        try {
            donService.effectuerUnDonParEmail(donRequest, authentication.getName());
            redirectAttributes.addAttribute("message", "Merci pour votre soutien ! Votre don a bien été pris en compte.");
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("message", e.getMessage());
        }

        return "redirect:/actions/detail/" + donRequest.getActionId();
    }

    @PostMapping("/supprimer/{id}")
    public String supprimerAction(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return "redirect:/login";
            }

            String email = auth.getName();
            ActionCharite action = actionService.obtenirActionParId(id);

            // Vérifier que l'utilisateur est propriétaire de l'action
            if (action.getOrganisation() == null ||
                action.getOrganisation().getUser() == null ||
                !email.equals(action.getOrganisation().getUser().getEmail())) {
                redirectAttributes.addAttribute("message", "Vous n'avez pas le droit de supprimer cette action.");
                return "redirect:/actions/detail/" + id;
            }

            actionService.supprimerAction(id);
            redirectAttributes.addAttribute("message", "Action supprimée avec succès.");
            return "redirect:/actions/dashboard";
        } catch (RuntimeException e) {
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/actions/detail/" + id;
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