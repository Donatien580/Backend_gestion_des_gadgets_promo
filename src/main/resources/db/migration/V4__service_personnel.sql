-- V4__service_personnel.sql

CREATE TABLE service (
    id_service              SERIAL PRIMARY KEY,
    libelle_service         VARCHAR(100) NOT NULL,
    code_service            VARCHAR(20) UNIQUE,
    matricule_responsable   VARCHAR(20),
    nom_responsable         VARCHAR(100),
    telephone_responsable   VARCHAR(20)
);

CREATE TABLE personnel (
    id_personnel    SERIAL PRIMARY KEY,
    nom             VARCHAR(50) NOT NULL,
    prenom          VARCHAR(50),
    matricule       VARCHAR(20) UNIQUE,
    telephone       VARCHAR(20),
    fonction        VARCHAR(100),
    actif           BOOLEAN DEFAULT TRUE,
    id_service      INTEGER NOT NULL REFERENCES service(id_service)
);

ALTER TABLE demande
    DROP COLUMN IF EXISTS service,
    DROP COLUMN IF EXISTS matricule_responsable,
    DROP COLUMN IF EXISTS nom_responsable;

ALTER TABLE demande
    ADD COLUMN IF NOT EXISTS id_service INTEGER REFERENCES service(id_service),
    ADD COLUMN IF NOT EXISTS nombre_personnels_impactes INTEGER;

DROP TABLE IF EXISTS receptionnaire;