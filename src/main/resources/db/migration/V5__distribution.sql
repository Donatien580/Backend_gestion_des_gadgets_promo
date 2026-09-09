ALTER TABLE distribution ADD COLUMN destinataire VARCHAR(255);
ALTER TABLE distribution ADD COLUMN etat VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE';
ALTER TABLE distribution ADD COLUMN date_generation_bordereau TIMESTAMP;

ALTER TABLE distribution DROP CONSTRAINT IF EXISTS distribution_id_utilisateur_fkey;
ALTER TABLE distribution DROP COLUMN IF EXISTS id_utilisateur;

ALTER TABLE distribution RENAME COLUMN signataire TO signe_par;

CREATE TABLE ligne_distribution (
    id_ligne              SERIAL PRIMARY KEY,
    quantite_distribuee   INTEGER NOT NULL,
    id_gadget             INTEGER NOT NULL REFERENCES gadget(id_gadget),
    id_distribution       INTEGER NOT NULL REFERENCES distribution(id_distribution)
);