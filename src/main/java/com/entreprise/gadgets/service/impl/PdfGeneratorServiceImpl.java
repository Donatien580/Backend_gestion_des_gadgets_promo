package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.exception.BusinessException;
import com.entreprise.gadgets.model.*;
import com.entreprise.gadgets.model.enums.TypeDistribution;
import com.entreprise.gadgets.service.PdfGeneratorService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    private static final String BORDEREAU_JRXML = "reports/bordereau_livraison.jrxml";

    @Override
    public byte[] genererBordereauPDF(Distribution distribution) {
        try {
            // 1. Charger le rapport JRXML
            InputStream jrxmlStream = new ClassPathResource(BORDEREAU_JRXML).getInputStream();
            JasperReport report = JasperCompileManager.compileReport(jrxmlStream);

            // 2. Charger le logo comme flux
			byte[] logoBytes;
			try (InputStream logoRaw = new ClassPathResource("images/images.png").getInputStream()) {
			    logoBytes = logoRaw.readAllBytes();
			} catch (IOException e) {
			    throw new BusinessException("Logo introuvable dans resources/images/Logo_SONABEL.png");
			}
			
			if (!estUnPngValide(logoBytes)) {
			    throw new BusinessException(
			        "Le fichier Logo_SONABEL.png n'a pas une signature PNG valide (probablement corrompu "
			        + "par le filtering Maven, ou pas vraiment un PNG). Ré-exporte-le proprement (GIMP/Paint) "
			        + "et remplace-le dans src/main/resources/images/."
			    );
			}

				Map<String, Object> parametres = new HashMap<>();
				parametres.put("logoStream", new ByteArrayInputStream(logoBytes));
				parametres.put("numeroBordereau", distribution.getNumeroBordereau());
				parametres.put("dateEnvoi", Timestamp.valueOf(distribution.getDateDistribution()));
				parametres.put("destinataire", resoudreDestinataire(distribution));
				parametres.put("responsable", "");
				
				JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(convertirLignes(distribution));
				JasperPrint print = JasperFillManager.fillReport(report, parametres, dataSource);
				return JasperExportManager.exportReportToPdf(print);
	    
        } catch (JRException e) {
            log.error("Erreur JasperReports :", e);
            throw new BusinessException("Erreur lors de la génération du bordereau PDF");
        } catch (Exception e) {
            log.error("Erreur inattendue :", e);
            throw new BusinessException("Erreur lors de la génération du bordereau PDF");
        }
    }

    private String resoudreDestinataire(Distribution distribution) {
        if (distribution.getTypeDistribution() == TypeDistribution.INTERNE) {
            String nom = distribution.getNomReceptionnaire();
            if (distribution.getPrenomReceptionnaire() != null && !distribution.getPrenomReceptionnaire().isBlank()) {
                nom = distribution.getPrenomReceptionnaire() + " " + nom;
            }
            String service = distribution.getServiceReceptionnaire();
            return service != null && !service.isBlank() ? nom + " (" + service + ")" : nom;
        }
        return distribution.getDestinataire() != null ? distribution.getDestinataire() : "";
    }

    private List<LigneBordereau> convertirLignes(Distribution distribution) {
        List<LigneBordereau> lignes = new ArrayList<>();
        for (LigneDistribution ligne : distribution.getLignes()) {
            lignes.add(new LigneBordereau(
                    ligne.getGadget().getLibelle(),
                    ligne.getQuantiteDistribuee()
            ));
        }
        return lignes;
    }

    // Classe JavaBean pour JasperReports
    public static class LigneBordereau {
        private String libelleGadget;
        private Integer quantiteDistribuee;

        public LigneBordereau(String libelleGadget, Integer quantiteDistribuee) {
            this.libelleGadget = libelleGadget;
            this.quantiteDistribuee = quantiteDistribuee;
        }

        public String getLibelleGadget() {
            return libelleGadget;
        }

        public Integer getQuantiteDistribuee() {
            return quantiteDistribuee;
        }
    }
    

    private boolean estUnPngValide(byte[] bytes) {
        int[] signature = {0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        if (bytes.length < signature.length) return false;
        for (int i = 0; i < signature.length; i++) {
            if ((bytes[i] & 0xFF) != signature[i]) return false;
        }
        return true;
    }

    private String apercuHexadecimal(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(8, bytes.length); i++) {
            sb.append(String.format("%02X ", bytes[i]));
        }
        return sb.toString().trim();
    }
}