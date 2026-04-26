package com.example.CharityProject.controllers;

import com.example.CharityProject.dto.UserRegistrationDTO; // Import du DTO
import com.example.CharityProject.entities.User;
import com.example.CharityProject.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // POST : http://localhost:8080/api/users/inscrire
    @PostMapping("/inscrire")
    // On change le type reçu : @RequestBody User -> @RequestBody UserRegistrationDTO
    public ResponseEntity<?> inscrire(@RequestBody UserRegistrationDTO userDto) {
        try {
            // On passe maintenant le DTO au service
            User nouvelUtilisateur = userService.inscrireUtilisateur(userDto);
            return new ResponseEntity<>(nouvelUtilisateur, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/tous")
    public ResponseEntity<List<User>> obtenirTous() {
        return ResponseEntity.ok(userService.obtenirTousLesUtilisateurs());
    }
}