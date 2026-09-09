-- Colonnes d'audit attendues par @CreationTimestamp / @UpdateTimestamp
ALTER TABLE inventaire ADD COLUMN date_creation TIMESTAMP NOT NULL DEFAULT now();
ALTER TABLE inventaire ADD COLUMN date_modification TIMESTAMP NOT NULL DEFAULT now();

-- id_utilisateur doit rester optionnel tant que Keycloak n'est pas branché :
-- InventaireService.creer() ne renseigne pas ce champ pour l'instant.
ALTER TABLE inventaire ALTER COLUMN id_utilisateur DROP NOT NULL;

-- Une seule ligne de comptage par (inventaire, gadget) — cf. LigneInventaire
ALTER TABLE ligne_inventaire
    ADD CONSTRAINT uk_ligne_inventaire_gadget UNIQUE (id_inventaire, id_gadget);

-- Utile pour les filtres par état (EN_COURS / TERMINE / VALIDE)
CREATE INDEX idx_inventaire_etat ON inventaire(etat);