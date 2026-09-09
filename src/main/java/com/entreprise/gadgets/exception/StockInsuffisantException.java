package com.entreprise.gadgets.exception;

/** Levée lorsqu'une sortie de stock est demandée pour une quantité supérieure au disponible. */
public class StockInsuffisantException extends BusinessException {

    public StockInsuffisantException(String message) {
        super(message);
    }
}
