package com.example.CharityProject.controllers;

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
    public ResponseEntity<?> inscrire(@RequestBody User user) {
        try {
            User nouvelUtilisateur = userService.inscrireUtilisateur(user);
            return new ResponseEntity<>(nouvelUtilisateur, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // GET : http://localhost:8080/api/users/tous
    @GetMapping("/tous")
    public ResponseEntity<List<User>> obtenirTous() {
        return ResponseEntity.ok(userService.obtenirTousLesUtilisateurs());
    }
}