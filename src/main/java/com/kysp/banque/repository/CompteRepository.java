package com.kysp.banque.repository;

import com.kysp.banque.models.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompteRepository extends JpaRepository<Compte, Long> {
    
    Compte findByAccno(String accno);

    
    Compte findByUsername(String username);


    boolean existsByAccno(String accno);
}
