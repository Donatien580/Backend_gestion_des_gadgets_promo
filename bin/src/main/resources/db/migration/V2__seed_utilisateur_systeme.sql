-- ============================================================================
-- V2__seed_utilisateur_systeme.sql
-- Compte technique utilisé pour attribuer les mouvements de stock et autres
-- actions tant que l'authentification Keycloak n'est pas branchée (cf.
-- UtilisateurCourantService). À terme, ce compte ne servira plus qu'aux
-- tâches vraiment automatisées (ex: tâches planifiées), plus à l'ensemble
-- du trafic comme c'est le cas aujourd'hui.
-- ============================================================================

INSERT INTO utilisateur (nom, prenom, email, actif, id_role)
SELECT 'Système', 'Automatisé', 'systeme@dcm.bf', TRUE, id_role
FROM role
WHERE nom = 'ADMIN';
