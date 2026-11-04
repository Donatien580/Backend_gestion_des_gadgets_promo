-- =============================================================================
-- Ajout des colonnes de gestion de correction sur la table ligne_distribution.
-- Permet la correction d'une distribution non exécutée, plusieurs fois de suite,
-- en conservant l'historique via une chaîne de remplacements.
-- =============================================================================

-- 1) Colonnes + contrainte en une seule instruction.
--    On met DEFAULT TRUE pour que les lignes existantes restent actives.
ALTER TABLE ligne_distribution
    ADD COLUMN actif BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN motif_correction VARCHAR(500),
    ADD COLUMN id_ligne_remplacee INTEGER,
    ADD CONSTRAINT fk_ligne_distribution_remplacee
        FOREIGN KEY (id_ligne_remplacee)
        REFERENCES ligne_distribution (id_ligne)
        ON DELETE SET NULL;

-- 2) On retire le DEFAULT : l'application doit fournir la valeur explicitement.
ALTER TABLE ligne_distribution
    ALTER COLUMN actif DROP DEFAULT;

-- 3) Index sur la clé étrangère : accélère la navigation dans la chaîne
--    (mapper, PDF, méthode trouverLigneActive).
CREATE INDEX idx_ligne_distribution_id_ligne_remplacee
    ON ligne_distribution (id_ligne_remplacee);

-- 4) Index partiel sur les lignes actives : les requêtes filtrent presque
--    toujours sur actif = TRUE, l'index est plus petit et plus rapide.
CREATE INDEX idx_ligne_distribution_actif_true
    ON ligne_distribution (id_distribution)
    WHERE actif = TRUE;