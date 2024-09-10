package com.kysp.banque.models;

import lombok.Data;

@Data
public class TransferRequest {
    private String accnoSource;
    private String accnoDestination;
    private long montant;
    private boolean payerFrais; // Ajouter cette propriété
}

