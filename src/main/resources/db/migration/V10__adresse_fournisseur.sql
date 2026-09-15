-- Adresse du fournisseur, obligatoire pour chaque approvisionnement.
ALTER TABLE approvisionnement
    ADD COLUMN adresse_fournisseur VARCHAR(255);

-- Backfill des lignes existantes avant de rendre la colonne obligatoire.
UPDATE approvisionnement
SET adresse_fournisseur = 'Non renseignée'
WHERE adresse_fournisseur IS NULL;

ALTER TABLE approvisionnement
    ALTER COLUMN adresse_fournisseur SET NOT NULL;