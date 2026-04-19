package com.example.CharityProject.controllers;

import com.example.CharityProject.entities.ActionCharite;
import com.example.CharityProject.services.ActionChariteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actions")
@RequiredArgsConstructor
public class ActionChariteController {

    private final ActionChariteService actionService;

    // POST : http://localhost:8080/api/actions/creer?organisationId=1
    @PostMapping("/creer")
    public ResponseEntity<?> creerAction(@RequestBody ActionCharite action, @RequestParam Long organisationId) {
        try {
            ActionCharite nouvelleAction = actionService.creerAction(action, organisationId);
            return new ResponseEntity<>(nouvelleAction, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // GET : http://localhost:8080/api/actions/categorie?nom=Santé
    @GetMapping("/categorie")
    public ResponseEntity<List<ActionCharite>> filtrerParCategorie(@RequestParam String nom) {
        return ResponseEntity.ok(actionService.filtrerParCategorie(nom));
    }

}