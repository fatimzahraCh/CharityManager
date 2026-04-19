package com.example.CharityProject.repositories;

import com.example.CharityProject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Essentiel pour la connexion et pour éviter les doublons à l'inscription
    Optional<User> findByEmail(String email);
}