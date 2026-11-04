package com.entreprise.gadgets.service;

import java.time.LocalDate;

public interface RapportService {
	byte[] genererRapportStockPdf(LocalDate dateDebut, LocalDate dateFin);
}
