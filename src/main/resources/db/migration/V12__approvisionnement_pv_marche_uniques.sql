-- 1) numero_marche est en bigint en base alors que l'entité attend un texte
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns
             WHERE table_name = 'approvisionnement'
               AND column_name = 'numero_marche'
               AND data_type IN ('bigint', 'integer', 'numeric')) THEN
    ALTER TABLE approvisionnement
      ALTER COLUMN numero_marche TYPE VARCHAR(30) USING numero_marche::VARCHAR;
  END IF;
END $$;

-- 2) Les valeurs vides deviennent NULL (plusieurs NULL sont autorisés par UNIQUE)
UPDATE approvisionnement SET numero_pv = NULL
 WHERE numero_pv IS NOT NULL AND TRIM(numero_pv) = '';
UPDATE approvisionnement SET numero_marche = NULL
 WHERE numero_marche IS NOT NULL AND TRIM(numero_marche) = '';

-- 3) Arrêt explicite et lisible s'il reste des doublons
DO $$
DECLARE d TEXT;
BEGIN
  SELECT string_agg(numero_pv, ', ') INTO d FROM (
    SELECT numero_pv FROM approvisionnement
    WHERE numero_pv IS NOT NULL GROUP BY numero_pv HAVING COUNT(*) > 1) x;
  IF d IS NOT NULL THEN
    RAISE EXCEPTION 'Doublons sur numero_pv : %', d;
  END IF;

  SELECT string_agg(numero_marche, ', ') INTO d FROM (
    SELECT numero_marche FROM approvisionnement
    WHERE numero_marche IS NOT NULL GROUP BY numero_marche HAVING COUNT(*) > 1) x;
  IF d IS NOT NULL THEN
    RAISE EXCEPTION 'Doublons sur numero_marche : %', d;
  END IF;
END $$;

-- 4) Contraintes d'unicité
ALTER TABLE approvisionnement
  ADD CONSTRAINT uk_approvisionnement_numero_pv UNIQUE (numero_pv);
ALTER TABLE approvisionnement
  ADD CONSTRAINT uk_approvisionnement_numero_marche UNIQUE (numero_marche);