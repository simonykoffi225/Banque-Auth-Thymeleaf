package com.kysp.banque.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kysp.banque.models.Compte;
import com.kysp.banque.models.FraisTransfert;
import com.kysp.banque.repository.CompteRepository;
import com.kysp.banque.repository.FraisTransfertRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class CompteService {

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FraisTransfertRepository fraisTransfertRepository;


    public boolean verifierMotDePasse(String plainTextPassword, String encodedPassword) {
        return passwordEncoder.matches(plainTextPassword, encodedPassword);
    }

    
    private String genererNumeroCompte() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public Compte findByUsername(String username) {
        return compteRepository.findByUsername(username);
    }
    

    public Compte ouvrirUnCompte(Compte compte) {
        
        String numeroCompte = genererNumeroCompte();
        while (compteRepository.existsByAccno(numeroCompte)) {
            numeroCompte = genererNumeroCompte();
        }
        compte.setAccno(numeroCompte);


        return compteRepository.save(compte);
    }

    public Compte afficherUnCompte(String accno) {
        return compteRepository.findByAccno(accno);
    }

    public List<Compte> afficherTousLesComptes() {
        return compteRepository.findAll();
    }

   

    public Compte depot(String accno, long montant) {
        Compte compte = compteRepository.findByAccno(accno);
        if (compte == null) {
            System.out.println("Compte introuvable avec le numéro de compte : " + accno);
            return null;
        }
        System.out.println("Solde avant dépôt : " + compte.getSolde());
        compte.setSolde(compte.getSolde() + montant);
        compteRepository.save(compte);
        System.out.println("Solde après dépôt : " + compte.getSolde());
        return compte;
    }
    

    public Compte retrait(String accno, long montant) {
        Compte compte = compteRepository.findByAccno(accno);
        if (compte != null && compte.getSolde() >= montant) {
            compte.setSolde(compte.getSolde() - montant);
            compteRepository.save(compte);
        }
        return compte;
    }

    public void supprimerUnCompte(String accno) {
        Compte compte = compteRepository.findByAccno(accno);
        if (compte != null) {
            compteRepository.delete(compte);
        }
    }
    

    public Compte findByAccno(String accno) {
        return compteRepository.findByAccno(accno);
    }
    
    public Compte modifierCompte(Compte updatedCompte) {
        if (updatedCompte.getId() == null) {
            throw new IllegalArgumentException("L'identifiant du compte ne peut pas être null");
        }
    
        Compte compte = compteRepository.findById(updatedCompte.getId()).orElse(null);
        if (compte != null) {
            compte.setEmail(updatedCompte.getEmail());
            compte.setAcType(updatedCompte.getAcType());
            compte.setUsername(updatedCompte.getUsername());
            compte.setSolde(updatedCompte.getSolde());
    
            if (!passwordEncoder.matches(updatedCompte.getPassword(), compte.getPassword())) {
                compte.setPassword(passwordEncoder.encode(updatedCompte.getPassword()));
            }
    
            compte.setRole(updatedCompte.getRole());
            compteRepository.save(compte);
        }
        return compte;
    }
    


    public Compte transfert(String accnoSource, String accnoDestination, long montant, long frais) {
        Compte compteSource = compteRepository.findByAccno(accnoSource);
        Compte compteDestination = compteRepository.findByAccno(accnoDestination);

        if (compteSource != null && compteDestination != null && compteSource.getSolde() >= montant + frais) {
            compteSource.setSolde(compteSource.getSolde() - montant - frais); // Déduire montant et frais
            compteDestination.setSolde(compteDestination.getSolde() + montant);
            compteRepository.save(compteSource);
            compteRepository.save(compteDestination);

            // Enregistrer les frais de transfert
            FraisTransfert fraisTransfert = new FraisTransfert();
            fraisTransfert.setCompteId(compteSource.getId());
            fraisTransfert.setDateTransfert(LocalDateTime.now());
            fraisTransfert.setMontantTransfert(montant);
            fraisTransfert.setFrais(frais);
            fraisTransfertRepository.save(fraisTransfert);

            return compteSource;
        } else {
            return null;
        }
    }
    
    
}
