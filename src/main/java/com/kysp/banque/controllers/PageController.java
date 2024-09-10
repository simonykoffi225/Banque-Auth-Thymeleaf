package com.kysp.banque.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kysp.banque.models.Compte;
import com.kysp.banque.service.CompteService;

@Controller
public class PageController {


    private final CompteService compteService;

    public PageController(CompteService compteService) {
        this.compteService = compteService;
    }

    @GetMapping("/defaultPage")
    public String defaultPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("Username: " + username);

        // Récupérer les détails du compte en fonction du nom d'utilisateur
        Compte compte = compteService.findByUsername(username);

        // Ajouter les attributs au modèle
        model.addAttribute("username", compte.getUsername());
        model.addAttribute("email", compte.getEmail());
        model.addAttribute("accno", compte.getAccno());
        model.addAttribute("ac_type", compte.getAcType());
        model.addAttribute("solde", compte.getSolde());

        return "defaultPage";
    }

    


    @GetMapping("/defaultPageAdmin")
    public String defaultPageAdmin(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("Username: " + username);

        // Récupérer les détails du compte en fonction du nom d'utilisateur
        Compte compte = compteService.findByUsername(username);

        // Ajouter les attributs au modèle
        model.addAttribute("username", compte.getUsername());
        model.addAttribute("email", compte.getEmail());
        model.addAttribute("accno", compte.getAccno());
        model.addAttribute("ac_type", compte.getAcType());
        model.addAttribute("solde", compte.getSolde());

        return "defaultPageAdmin";
    }
}

   

    
    

