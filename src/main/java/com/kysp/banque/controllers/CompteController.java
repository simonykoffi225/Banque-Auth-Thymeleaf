package com.kysp.banque.controllers;

import com.kysp.banque.models.Compte;
import com.kysp.banque.models.TransferRequest;
import com.kysp.banque.service.CompteService;

import lombok.AllArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/banque")
@AllArgsConstructor
public class CompteController {

    private CompteService compteService;

    
    @GetMapping("/ouvrir")
    public String afficherFormulaireOuvertureCompte(Model model) {
        model.addAttribute("compte", new Compte());
        return "ouvrirCompte"; 
    }

    
    @PostMapping("/ouvrir")
    public String ouvrirUnCompte(@ModelAttribute Compte compte, Model model) {
        Compte nouveauCompte = compteService.ouvrirUnCompte(compte);
        model.addAttribute("compte", nouveauCompte);
        return "redirect:/banque/comptes"; 
    }

    

    @GetMapping("/listeComptes")
    public String afficherTousLesComptes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        model.addAttribute("username", username);
        List<Compte> comptes = compteService.afficherTousLesComptes();
        model.addAttribute("comptes", comptes);
        return "listeComptes"; 
    }

    
    @GetMapping("/compte/{accno}")
    public String afficherUnCompte(@PathVariable String accno, Model model) {
        Compte compte = compteService.afficherUnCompte(accno);
        if (compte != null) {
            model.addAttribute("compte", compte);
            return "detailsCompte"; 
        } else {
            return "redirect:/banque/comptes"; 
        }
    }

   
    @GetMapping("/depot/{accno}")
    public String afficherFormulaireDepot(@PathVariable String accno, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    model.addAttribute("username", username);
        model.addAttribute("accno", accno);
        return "depot";
    }



    @PostMapping("/depot")
    public String effectuerDepot(@RequestParam("accno") String accno, @RequestParam("montant") long montant, Model model) {
        System.out.println("Montant reçu : " + montant);
        System.out.println("Numéro de compte : " + accno);
        
        Compte compte = compteService.depot(accno, montant);
        if (compte != null) {
            model.addAttribute("message", "Le dépôt a été effectué avec succès !");
        } else {
            model.addAttribute("error", "Erreur lors du dépôt. Compte introuvable.");
        }
        return "depot";
    }
    
    
    @GetMapping("/retrait/{accno}")
    public String afficherFormulaireRetrait(@PathVariable String accno, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    model.addAttribute("username", username);
        model.addAttribute("accno", accno);
        return "retrait";
    }

    @PostMapping("/retrait/{accno}")
    public String effectuerRetrait(@PathVariable String accno, @RequestParam("montant") long montant, Model model) {
    System.out.println("Montant reçu : " + montant);
    System.out.println("Numéro de compte : " + accno);
    
    Compte compte = compteService.retrait(accno, montant);
    if (compte != null) {
        model.addAttribute("message", "Le retrait a été effectué avec succès !");
    } else {
        model.addAttribute("error", "Erreur lors du retrait. Veuillez vérifier le montant et réessayer.");
    }
    return "retrait";
    }

    @DeleteMapping("/supprimer/{accno}")
    public String supprimerUnCompte(@PathVariable String accno) {
        compteService.supprimerUnCompte(accno);
        return "redirect:/banque/listeComptes";
    }


    @GetMapping("/modifierForm/{accno}")
    public String showModifierForm(@PathVariable String accno, Model model) {
        Compte compte = compteService.findByAccno(accno);
        model.addAttribute("compte", compte);
        return "modifierCompte";
    }

    @PostMapping("/modifier")
public String modifierCompte(@ModelAttribute Compte compte, Model model) {
    if (compte.getId() == null) {
        model.addAttribute("error", "L'identifiant du compte est requis");
        return "modifierCompte";
    }
    Compte updatedCompte = compteService.modifierCompte(compte);
    if (updatedCompte == null) {
        model.addAttribute("error", "Le compte n'a pas été trouvé pour modification");
        return "modifierCompte";
    }
    return "redirect:/banque/listeComptes";
}




    @GetMapping("/transfert/{accno}")
    public String afficherFormulaireTransfert(@PathVariable String accno, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    model.addAttribute("username", username);
    model.addAttribute("accno", accno);
    return "transfert"; 
    }

    @PostMapping("/transfert")
public String effectuerTransfert(@ModelAttribute TransferRequest transferRequest, Model model) {
    boolean payerFrais = transferRequest.isPayerFrais(); // Ajouter cette propriété à TransferRequest
    long montant = transferRequest.getMontant();
    long frais = payerFrais ? montant / 100 : 0; // 1% des frais

    // Si les frais sont payés par l'utilisateur
    if (payerFrais) {
        montant -= frais; // Réduire le montant du transfert
    }

    Compte compteSource = compteService.transfert(
        transferRequest.getAccnoSource(),
        transferRequest.getAccnoDestination(),
        montant,
        frais
    );

    if (compteSource != null) {
        model.addAttribute("message", "Le transfert a été effectué avec succès !");
    } else {
        model.addAttribute("error", "Erreur lors du transfert. Veuillez vérifier les informations et réessayer.");
    }

    return "transfert";
}


    @GetMapping("/profil")
    public String afficherProfil(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Compte compte = compteService.findByUsername(username);

        model.addAttribute("username", compte.getUsername());
        model.addAttribute("email", compte.getEmail());
        model.addAttribute("accno", compte.getAccno());
        model.addAttribute("ac_type", compte.getAcType());
        model.addAttribute("solde", compte.getSolde());

        return "profil";
    }

}
