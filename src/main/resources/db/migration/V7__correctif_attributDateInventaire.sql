ALTER TABLE inventaire
    ALTER COLUMN date_inventaire TYPE DATE USING date_inventaire::DATE;