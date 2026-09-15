-- ----------------------------------------------------------------------------
-- 1. Module Catalogue : unicité du libellé
-- ----------------------------------------------------------------------------

-- Categorie : un même libellé (insensible à la casse) ne peut exister deux fois
CREATE UNIQUE INDEX uk_categorie_libelle ON categorie (LOWER(libelle));

-- Gadget : pas de table de "variantes" séparée -> l'unicité porte sur le couple
-- (libelle, designation). designation étant nullable, on normalise les NULL
-- avec COALESCE pour éviter que deux gadgets "Stylo" sans designation coexistent.
CREATE UNIQUE INDEX uk_gadget_libelle_designation
    ON gadget (LOWER(libelle), LOWER(COALESCE(designation, '')));


-- ----------------------------------------------------------------------------
-- 2. Module Approvisionnement : correction de saisie
-- ----------------------------------------------------------------------------

-- Statut global : permet de voir en un coup d'oeil si l'approvisionnement
-- a fait l'objet d'une correction après son enregistrement initial.
ALTER TABLE approvisionnement
    ADD COLUMN statut VARCHAR(20) NOT NULL DEFAULT 'ENREGISTRE',
    ADD COLUMN numero_marche VARCHAR(30);

-- Une ligne erronée n'est jamais supprimée ni écrasée : elle est désactivée
-- (actif = false) et remplacée par une nouvelle ligne qui pointe vers elle,
-- avec le motif de la correction. Trace complète conservée.
ALTER TABLE ligne_approvisionnement
    DROP COLUMN IF EXISTS quantite_conforme,
    ADD COLUMN actif BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN id_ligne_remplacee INTEGER REFERENCES ligne_approvisionnement(id_ligne),
    ADD COLUMN motif_correction TEXT;

CREATE INDEX idx_ligne_appro_remplacee ON ligne_approvisionnement(id_ligne_remplacee);

-- type_mouvement passe de VARCHAR(10) à VARCHAR(20) : "CORRECTION" (10 caractères)
-- tient tout juste, on prend de la marge pour de futures valeurs d'enum.
ALTER TABLE mouvement_stock
    ALTER COLUMN type_mouvement TYPE VARCHAR(20);


-- ----------------------------------------------------------------------------
-- 3. Module Demande : plus de lignes de demande, plus de Personnel/Service
-- ----------------------------------------------------------------------------

-- Les demandeurs ne précisent plus quels gadgets ni quelle quantité :
-- c'est l'agent qui consulte le stock disponible et distribue en conséquence.
DROP TABLE IF EXISTS ligne_demande;

-- Les informations du demandeur (interne ou externe) sont saisies directement
-- sur la demande, sans passer par un référentiel Personnel/Service.
ALTER TABLE demande
    ADD COLUMN nom_demandeur VARCHAR(100),
    ADD COLUMN prenom_demandeur VARCHAR(100),
    ADD COLUMN telephone_demandeur VARCHAR(20),
    ADD COLUMN matricule_demandeur VARCHAR(20),
    ADD COLUMN service_demandeur VARCHAR(100),
    ADD COLUMN structure_demandeur VARCHAR(150),
    ADD COLUMN date_traitement TIMESTAMP;

-- Reprise des données existantes avant suppression des anciennes colonnes
UPDATE demande SET structure_demandeur = structure WHERE structure IS NOT NULL;
UPDATE demande SET nom_demandeur = representant WHERE representant IS NOT NULL;
-- on leur donne une valeur de repli pour pouvoir passer nom_demandeur en NOT NULL.
UPDATE demande SET nom_demandeur = 'Non renseigné' WHERE nom_demandeur IS NULL;
UPDATE demande SET telephone_demandeur = telephone WHERE telephone IS NOT NULL;

ALTER TABLE demande
    DROP COLUMN IF EXISTS structure,
    DROP COLUMN IF EXISTS representant,
    DROP COLUMN IF EXISTS telephone,
    DROP COLUMN IF EXISTS id_service,
    DROP COLUMN IF EXISTS nombre_personnels_impactes;

ALTER TABLE demande
    ALTER COLUMN nom_demandeur SET NOT NULL;

DROP TABLE IF EXISTS personnel;
DROP TABLE IF EXISTS service;


-- ----------------------------------------------------------------------------
-- 4. Module Distribution : dotations (sans demande) + réceptionnaire interne
-- ----------------------------------------------------------------------------

-- Une distribution n'est plus obligatoirement liée à une demande.
ALTER TABLE distribution
    ALTER COLUMN id_demande DROP NOT NULL;

ALTER TABLE distribution
    ADD COLUMN est_dotation BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN matricule_receptionnaire VARCHAR(20),
    ADD COLUMN nom_receptionnaire VARCHAR(100),
    ADD COLUMN prenom_receptionnaire VARCHAR(100),
    ADD COLUMN service_receptionnaire VARCHAR(100),
    ADD COLUMN nombre_personnes INTEGER;

-- Cohérence : soit rattachée à une demande, soit explicitement une dotation.
ALTER TABLE distribution
    ADD CONSTRAINT chk_distribution_source
    CHECK (id_demande IS NOT NULL OR est_dotation = TRUE);

-- Suppression du circuit de signature : plus aucune colonne "signature" sur la table.
ALTER TABLE distribution
    DROP COLUMN IF EXISTS date_signature,
    DROP COLUMN IF EXISTS signe_par;

-- Table héritée de V1, jamais mappée par une entité JPA (morte) : les infos
-- du réceptionnaire sont désormais directement sur distribution.
DROP TABLE IF EXISTS receptionnaire;