package com.example.CharityProject.controllers;

import com.example.CharityProject.entities.Don;
import com.example.CharityProject.services.DonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dons")
@RequiredArgsConstructor
public class DonController {

    private final DonService donService;

    // POST : http://localhost:8080/api/dons/effectuer?userId=1&actionId=1&montant=500.0&transactionId=TX12345
    @PostMapping("/effectuer")
    public ResponseEntity<?> effectuerDon(
            @RequestParam Long userId,
            @RequestParam Long actionId,
            @RequestParam Double montant,
            @RequestParam String transactionId) {
        try {
            Don don = donService.effectuerUnDon(userId, actionId, montant, transactionId);
            return new ResponseEntity<>(don, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // GET : http://localhost:8080/api/dons/historique/1
    @GetMapping("/historique/{userId}")
    public ResponseEntity<List<Don>> historiqueDons(@PathVariable Long userId) {
        return ResponseEntity.ok(donService.obtenirHistoriqueDons(userId));
    }
}