-- Empêche de renvoyer le même e-mail à chaque mouvement de stock tant que
-- le seuil concerné n'a pas été "réarmé" par un réapprovisionnement suffisant.
ALTER TABLE gadget
    ADD COLUMN alerte_avertissement_envoyee BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN alerte_critique_envoyee BOOLEAN NOT NULL DEFAULT FALSE;
   