-- =========================================================
-- 1. AJOUT DES COLONNES DANS DEMANDE
-- =========================================================

ALTER TABLE demande
    ADD COLUMN IF NOT EXISTS service VARCHAR(100);

ALTER TABLE demande
    ADD COLUMN IF NOT EXISTS matricule_responsable VARCHAR(20);

ALTER TABLE demande
    ADD COLUMN IF NOT EXISTS nom_responsable VARCHAR(100);

ALTER TABLE demande
    ADD COLUMN IF NOT EXISTS structure VARCHAR(100);

ALTER TABLE demande
    ADD COLUMN IF NOT EXISTS representant VARCHAR(100);

ALTER TABLE demande
    ADD COLUMN IF NOT EXISTS telephone VARCHAR(20);


-- =========================================================
-- 2. TYPE DE DEMANDE
-- =========================================================

ALTER TABLE demande
    ADD COLUMN IF NOT EXISTS type_demande VARCHAR(20);


-- =========================================================
-- 3. TRANSFERT DES DEMANDES INTERNES
-- =========================================================

UPDATE demande d
SET
    type_demande = 'INTERNE',
    service = di.service,
    nom_responsable = di.responsable
FROM demande_interne di
WHERE di.id_demande = d.id_demande;


-- =========================================================
-- 4. TRANSFERT DES DEMANDES EXTERNES
-- =========================================================

UPDATE demande d
SET
    type_demande = 'EXTERNE',
    structure = de.structure,
    representant = de.representant,
    telephone = de.telephone
FROM demande_externe de
WHERE de.id_demande = d.id_demande;


-- =========================================================
-- 5. CONTRAINTE
-- =========================================================

ALTER TABLE demande
    ALTER COLUMN type_demande SET NOT NULL;


-- =========================================================
-- 6. SUPPRESSION DES ANCIENNES TABLES
-- =========================================================

DROP TABLE IF EXISTS demande_interne;

DROP TABLE IF EXISTS demande_externe;