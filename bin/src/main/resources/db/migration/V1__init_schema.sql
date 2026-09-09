-- ============================================================================
-- V1__init_schema.sql
-- Schéma initial - Application de gestion des gadgets promotionnels (DCM)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Module 0 : Utilisateurs & rôles
-- ----------------------------------------------------------------------------
CREATE TABLE role (
    id_role     SERIAL PRIMARY KEY,
    nom         VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE utilisateur (
    id_utilisateur    SERIAL PRIMARY KEY,
    nom               VARCHAR(50) NOT NULL,
    prenom            VARCHAR(50) NOT NULL,
    email             VARCHAR(100) NOT NULL UNIQUE,
    mot_de_passe_hash VARCHAR(255),
    actif             BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation     TIMESTAMP NOT NULL DEFAULT now(),
    id_role           INTEGER NOT NULL REFERENCES role(id_role)
);

-- ----------------------------------------------------------------------------
-- Module 1 : Catalogue
-- ----------------------------------------------------------------------------
CREATE TABLE categorie (
    id_categorie SERIAL PRIMARY KEY,
    libelle      VARCHAR(50) NOT NULL,
    description  TEXT
);

CREATE TABLE gadget (
    id_gadget           SERIAL PRIMARY KEY,
    libelle             VARCHAR(100) NOT NULL,
    designation         VARCHAR(30),
    description         TEXT,
    seuil_alerte        INTEGER NOT NULL DEFAULT 50,
    photo_gadget        VARCHAR(255),
    quantite_disponible INTEGER NOT NULL DEFAULT 0,
    etat                VARCHAR(30) DEFAULT 'DISPONIBLE',
    actif               BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation       TIMESTAMP NOT NULL DEFAULT now(),
    date_modification   TIMESTAMP NOT NULL DEFAULT now(),
    id_categorie        INTEGER NOT NULL REFERENCES categorie(id_categorie)
);
CREATE INDEX idx_gadget_categorie ON gadget(id_categorie);
CREATE INDEX idx_gadget_actif ON gadget(actif);

-- ----------------------------------------------------------------------------
-- Module 2 : Approvisionnements
-- ----------------------------------------------------------------------------
CREATE TABLE approvisionnement (
    id_approvisionnement SERIAL PRIMARY KEY,
    date_reception        TIMESTAMP NOT NULL,
    fournisseur           VARCHAR(100) NOT NULL,
    numero_pv             VARCHAR(30),
    observations          TEXT
);

CREATE TABLE ligne_approvisionnement (
    id_ligne              SERIAL PRIMARY KEY,
    quantite_commandee     INTEGER,
    quantite_recue          INTEGER NOT NULL,
    quantite_conforme       INTEGER,
    quantite_defectueuse    INTEGER,
    observation_qualite     VARCHAR(255),
    id_gadget               INTEGER NOT NULL REFERENCES gadget(id_gadget),
    id_approvisionnement    INTEGER NOT NULL REFERENCES approvisionnement(id_approvisionnement)
);
CREATE INDEX idx_ligne_appro_gadget ON ligne_approvisionnement(id_gadget);
CREATE INDEX idx_ligne_appro_appro ON ligne_approvisionnement(id_approvisionnement);

CREATE TABLE incident (
    id_incident          SERIAL PRIMARY KEY,
    type_incident         VARCHAR(30) NOT NULL,
    description           TEXT,
    date_signalement      TIMESTAMP NOT NULL DEFAULT now(),
    statut                VARCHAR(20) NOT NULL DEFAULT 'EN_COURS',
    id_approvisionnement  INTEGER NOT NULL REFERENCES approvisionnement(id_approvisionnement)
);

-- ----------------------------------------------------------------------------
-- Module 3 : Demandes (héritage JOINED : demande / demande_interne / demande_externe)
-- ----------------------------------------------------------------------------
CREATE TABLE demande (
    id_demande       SERIAL PRIMARY KEY,
    numero_demande   VARCHAR(20) NOT NULL UNIQUE,
    objet            VARCHAR(255) NOT NULL,
    type_demande     VARCHAR(30) NOT NULL,
    date_demande     TIMESTAMP NOT NULL DEFAULT now(),
    date_souhaitee   DATE,
    date_validation  TIMESTAMP,
    etat             VARCHAR(30) NOT NULL DEFAULT 'EN_ATTENTE',
    motif_refus      TEXT,
    observations     TEXT,
    id_utilisateur   INTEGER NOT NULL REFERENCES utilisateur(id_utilisateur),
    id_agent_affecte INTEGER REFERENCES utilisateur(id_utilisateur)
);
CREATE INDEX idx_demande_etat ON demande(etat);
CREATE INDEX idx_demande_agent_affecte ON demande(id_agent_affecte);

CREATE TABLE demande_interne (
    id_demande  INTEGER PRIMARY KEY REFERENCES demande(id_demande),
    service     VARCHAR(100) NOT NULL,
    responsable VARCHAR(100) NOT NULL
);

CREATE TABLE demande_externe (
    id_demande    INTEGER PRIMARY KEY REFERENCES demande(id_demande),
    structure     VARCHAR(100) NOT NULL,
    representant  VARCHAR(100) NOT NULL,
    telephone     VARCHAR(20)
);

CREATE TABLE ligne_demande (
    id_ligne           SERIAL PRIMARY KEY,
    quantite_demandee   INTEGER NOT NULL,
    quantite_accordee   INTEGER,
    id_gadget           INTEGER NOT NULL REFERENCES gadget(id_gadget),
    id_demande          INTEGER NOT NULL REFERENCES demande(id_demande)
);
CREATE INDEX idx_ligne_demande_demande ON ligne_demande(id_demande);

CREATE TABLE piece_justificative (
    id_piece        SERIAL PRIMARY KEY,
    nom_fichier      VARCHAR(255) NOT NULL,
    chemin_fichier   VARCHAR(500) NOT NULL,
    type_fichier     VARCHAR(50),
    taille           BIGINT,
    date_upload      TIMESTAMP NOT NULL DEFAULT now(),
    id_demande       INTEGER NOT NULL UNIQUE REFERENCES demande(id_demande)
);

-- ----------------------------------------------------------------------------
-- Module 4 : Distributions
-- ----------------------------------------------------------------------------
CREATE TABLE distribution (
    id_distribution     SERIAL PRIMARY KEY,
    numero_bordereau     VARCHAR(20) NOT NULL UNIQUE,
    date_distribution    TIMESTAMP NOT NULL,
    type_distribution    VARCHAR(10) NOT NULL,
    motif                TEXT,
    date_signature       TIMESTAMP,
    signataire           VARCHAR(100),
    id_demande           INTEGER NOT NULL UNIQUE REFERENCES demande(id_demande),
    id_utilisateur       INTEGER NOT NULL REFERENCES utilisateur(id_utilisateur)
);

CREATE TABLE receptionnaire (
    id_receptionnaire  SERIAL PRIMARY KEY,
    nom                 VARCHAR(50) NOT NULL,
    prenom              VARCHAR(50),
    matricule           VARCHAR(20),
    service             VARCHAR(100),
    telephone           VARCHAR(20),
    quantite_recue      INTEGER NOT NULL,
    id_distribution     INTEGER NOT NULL REFERENCES distribution(id_distribution),
    id_gadget           INTEGER NOT NULL REFERENCES gadget(id_gadget)
);
CREATE INDEX idx_receptionnaire_distribution ON receptionnaire(id_distribution);

CREATE TABLE retour_gadget (
    id_retour        SERIAL PRIMARY KEY,
    date_retour       TIMESTAMP NOT NULL DEFAULT now(),
    quantite          INTEGER NOT NULL,
    motif             TEXT,
    id_gadget         INTEGER NOT NULL REFERENCES gadget(id_gadget),
    id_distribution   INTEGER REFERENCES distribution(id_distribution)
);

-- ----------------------------------------------------------------------------
-- Journal des mouvements de stock (traçabilité - toutes entrées/sorties)
-- ----------------------------------------------------------------------------
CREATE TABLE mouvement_stock (
    id_mouvement    SERIAL PRIMARY KEY,
    type_mouvement   VARCHAR(10) NOT NULL,
    quantite         INTEGER NOT NULL,
    stock_avant      INTEGER NOT NULL,
    stock_apres      INTEGER NOT NULL,
    motif            TEXT,
    date_mouvement   TIMESTAMP NOT NULL DEFAULT now(),
    id_gadget        INTEGER NOT NULL REFERENCES gadget(id_gadget),
    id_utilisateur   INTEGER NOT NULL REFERENCES utilisateur(id_utilisateur),
    id_reference     INTEGER,
    type_reference   VARCHAR(30)
);
CREATE INDEX idx_mouvement_gadget ON mouvement_stock(id_gadget);
CREATE INDEX idx_mouvement_date ON mouvement_stock(date_mouvement);

-- ----------------------------------------------------------------------------
-- Module 5 : Inventaires
-- ----------------------------------------------------------------------------
CREATE TABLE inventaire (
    id_inventaire    SERIAL PRIMARY KEY,
    date_inventaire   TIMESTAMP NOT NULL,
    type_inventaire   VARCHAR(20) NOT NULL,
    etat              VARCHAR(20) NOT NULL DEFAULT 'EN_COURS',
    observations      TEXT,
    id_utilisateur    INTEGER NOT NULL REFERENCES utilisateur(id_utilisateur)
);

CREATE TABLE ligne_inventaire (
    id_ligne            SERIAL PRIMARY KEY,
    stock_theorique      INTEGER NOT NULL,
    stock_reel           INTEGER NOT NULL,
    ecart                INTEGER NOT NULL,
    justification        TEXT,
    validation_justif    BOOLEAN NOT NULL DEFAULT FALSE,
    id_gadget            INTEGER NOT NULL REFERENCES gadget(id_gadget),
    id_inventaire        INTEGER NOT NULL REFERENCES inventaire(id_inventaire)
);
CREATE INDEX idx_ligne_inventaire_inventaire ON ligne_inventaire(id_inventaire);

-- ----------------------------------------------------------------------------
-- Données de référence : rôles applicatifs (correspondent aux rôles Keycloak)
-- ----------------------------------------------------------------------------
INSERT INTO role (nom, description) VALUES
    ('ADMIN', 'Gère les paramétrages, les utilisateurs et les données techniques'),
    ('AGENT_SAISIE', 'Saisit les demandes internes/externes, uploade les pièces justificatives'),
    ('CHEF_DEPARTEMENT', 'Valide/refuse/affecte les demandes, reçoit les alertes stratégiques'),
    ('CHEF_SERVICE', 'Traite les demandes, vérifie le stock, génère et signe les bordereaux'),
    ('GESTIONNAIRE_STOCK', 'Gère le catalogue, les approvisionnements, les distributions, les inventaires');
