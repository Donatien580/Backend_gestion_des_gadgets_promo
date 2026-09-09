package com.entreprise.gadgets.service;

import com.entreprise.gadgets.model.Gadget;
import com.entreprise.gadgets.model.Utilisateur;

/**
 * Aucun autre service ne doit appeler gadget.augmenterStock()/diminuerStock()
 * directement en dehors de cette classe.
 */
public interface StockService {

    
    void enregistrerEntree(Gadget gadget, int quantite, Utilisateur utilisateur, String motif,
                            Integer idReference, String typeReference);

  
    void enregistrerSortie(Gadget gadget, int quantite, Utilisateur utilisateur, String motif,
                            Integer idReference, String typeReference);

   
    void enregistrerRetour(Gadget gadget, int quantite, Utilisateur utilisateur, String motif,
                            Integer idReference, String typeReference);

    /**
     * Régularisation suite à un inventaire.
     * @param ecart stockTheorique - stockReel (positif = stock à la baisse,
     *              négatif = stock à la hausse).
     */
    void enregistrerAjustementInventaire(Gadget gadget, int ecart, Utilisateur utilisateur, String motif,
                                          Integer idReference, String typeReference);
}
