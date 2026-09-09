package com.entreprise.gadgets.service;

import org.springframework.stereotype.Service;

import com.entreprise.gadgets.model.Distribution;

@Service
public interface PdfGeneratorService {
   byte[] genererBordereauPDF(Distribution distribution);
}
