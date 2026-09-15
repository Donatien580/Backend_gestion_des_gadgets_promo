package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.model.Gadget;
import com.entreprise.gadgets.model.MouvementStock;
import com.entreprise.gadgets.model.Utilisateur;
import com.entreprise.gadgets.model.enums.TypeMouvement;
import com.entreprise.gadgets.repository.MouvementStockRepository;
import com.entreprise.gadgets.service.NotificationSeuilService;
import com.entreprise.gadgets.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final MouvementStockRepository mouvementStockRepository;
    private final NotificationSeuilService notificationSeuilService;

    @Override
    @Transactional
    public void enregistrerEntree(Gadget gadget, int quantite, Utilisateur utilisateur, String motif,
                                   Integer idReference, String typeReference) {
        int stockAvant = gadget.getQuantiteDisponible();
        gadget.augmenterStock(quantite);
        journaliser(TypeMouvement.ENTREE, gadget, quantite, stockAvant, utilisateur, motif, idReference, typeReference);
    }

    @Override
    @Transactional
    public void enregistrerSortie(Gadget gadget, int quantite, Utilisateur utilisateur, String motif,
                                   Integer idReference, String typeReference) {
        int stockAvant = gadget.getQuantiteDisponible();
        gadget.diminuerStock(quantite); // lève StockInsuffisantException si besoin
        journaliser(TypeMouvement.SORTIE, gadget, quantite, stockAvant, utilisateur, motif, idReference, typeReference);
    }

    @Override
    @Transactional
    public void enregistrerRetour(Gadget gadget, int quantite, Utilisateur utilisateur, String motif,
                                   Integer idReference, String typeReference) {
        int stockAvant = gadget.getQuantiteDisponible();
        gadget.augmenterStock(quantite);
        journaliser(TypeMouvement.RETOUR, gadget, quantite, stockAvant, utilisateur, motif, idReference, typeReference);
    }

    @Override
    @Transactional
    public void enregistrerAjustementInventaire(Gadget gadget, int ecart, Utilisateur utilisateur, String motif,
                                                 Integer idReference, String typeReference) {
        int stockAvant = gadget.getQuantiteDisponible();
        // ecart = stockTheorique - stockReel (cf. LigneInventaire.calculerEcart) :
        // positif -> moins d'exemplaires trouvés que prévu -> baisse du stock enregistré.
        // négatif -> plus d'exemplaires trouvés que prévu -> hausse du stock enregistré.
        if (ecart > 0) {
            gadget.diminuerStock(ecart);
        } else if (ecart < 0) {
            gadget.augmenterStock(-ecart);
        } else {
            return; // Aucun écart : rien à ajuster, pas de mouvement à journaliser.
        }
        journaliser(TypeMouvement.INVENTAIRE, gadget, Math.abs(ecart), stockAvant, utilisateur, motif, idReference, typeReference);
    }
    
    @Override
    @Transactional
    public void enregistrerCorrection(Gadget gadget, int delta, Utilisateur utilisateur, String motif,
                                       Integer idReference, String typeReference) {
        if (delta == 0) {
            return; // Correction sans impact sur le stock (ex. juste l'observation qui change)
        }
        int stockAvant = gadget.getQuantiteDisponible();
        if (delta > 0) {
            gadget.augmenterStock(delta);
        } else {
            gadget.diminuerStock(-delta);
        }
        journaliser(TypeMouvement.CORRECTION, gadget, Math.abs(delta), stockAvant, utilisateur, motif, idReference, typeReference);
    }

    private void journaliser(TypeMouvement type, Gadget gadget, int quantite, int stockAvant,
                              Utilisateur utilisateur, String motif, Integer idReference, String typeReference) {
        MouvementStock mouvement = MouvementStock.builder()
            .typeMouvement(type)
            .quantite(quantite)
            .stockAvant(stockAvant)
            .stockApres(gadget.getQuantiteDisponible())
            .motif(motif)
            .dateMouvement(LocalDateTime.now())
            .gadget(gadget)
            .utilisateur(utilisateur)
            .idReference(idReference)
            .typeReference(typeReference)
            .build();
        mouvementStockRepository.save(mouvement);
        
        notificationSeuilService.verifierSeuils(gadget);
    }
}
