package com.entreprise.gadgets.service;

import com.entreprise.gadgets.exception.StockInsuffisantException;
import com.entreprise.gadgets.model.Gadget;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Vérifie la logique de gestion du stock portée directement par l'entité Gadget
 * (règles structurelles simples : jamais de stock négatif, calcul du seuil d'alerte).
 * La logique d'orchestration (création des MouvementStock, etc.) sera testée au
 * niveau de StockService une fois celui-ci implémenté.
 */
class GadgetStockLogiqueTest {

    @Test
    void augmenterStock_devrait_incrementer_la_quantite_disponible() {
        Gadget gadget = Gadget.builder().libelle("T-shirt").quantiteDisponible(10).seuilAlerte(5).build();

        gadget.augmenterStock(20);

        assertThat(gadget.getQuantiteDisponible()).isEqualTo(30);
    }

    @Test
    void diminuerStock_devrait_decrementer_quand_le_stock_est_suffisant() {
        Gadget gadget = Gadget.builder().libelle("Casquette").quantiteDisponible(30).seuilAlerte(5).build();

        gadget.diminuerStock(10);

        assertThat(gadget.getQuantiteDisponible()).isEqualTo(20);
    }

    @Test
    void diminuerStock_devrait_lever_une_exception_si_stock_insuffisant() {
        Gadget gadget = Gadget.builder().libelle("Stylo").quantiteDisponible(5).seuilAlerte(5).build();

        assertThatThrownBy(() -> gadget.diminuerStock(10))
            .isInstanceOf(StockInsuffisantException.class)
            .hasMessageContaining("Stylo");
    }

    @Test
    void estSousSeuilAlerte_devrait_etre_vrai_quand_stock_egal_ou_inferieur_au_seuil() {
        Gadget gadget = Gadget.builder().libelle("Porte-clés").quantiteDisponible(5).seuilAlerte(5).build();

        assertThat(gadget.estSousSeuilAlerte()).isTrue();
    }

    @Test
    void estSousSeuilAlerte_devrait_etre_faux_quand_stock_superieur_au_seuil() {
        Gadget gadget = Gadget.builder().libelle("Porte-clés").quantiteDisponible(51).seuilAlerte(50).build();

        assertThat(gadget.estSousSeuilAlerte()).isFalse();
    }
}
