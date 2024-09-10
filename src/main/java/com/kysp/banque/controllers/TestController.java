package com.kysp.banque.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kysp.banque.repository.CompteRepository;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class TestController {

    private final PasswordEncoder passwordEncoder;
    private final CompteRepository compteRepository;

    @GetMapping("/verifierMotDePasse")
    public ResponseEntity<String> verifierMotDePasse(
            @RequestParam String username,
            @RequestParam String password) {

        com.kysp.banque.models.Compte compte = compteRepository.findByUsername(username);
        if (compte != null) {
            boolean isPasswordMatch = passwordEncoder.matches(password, compte.getPassword());
            return ResponseEntity.ok("Password match: " + isPasswordMatch);
        }
        return ResponseEntity.notFound().build();
    }
}
