package com.kysp.banque.controllers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kysp.banque.models.Compte;
import com.kysp.banque.service.CompteService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/signup")
@AllArgsConstructor
public class SignupController {

    private final CompteService compteService;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    public String showSignupForm(Model model) {
        model.addAttribute("compte", new Compte());
        return "signup";
    }

    @PostMapping
    public String signup(@ModelAttribute Compte compte) {
        compte.setPassword(passwordEncoder.encode(compte.getPassword()));
        compte.setRole("ROLE_USER");
        compteService.ouvrirUnCompte(compte);
        return "redirect:/login";
    }
}
