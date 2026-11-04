package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.exception.BusinessException;
import com.entreprise.gadgets.model.Distribution;
import com.entreprise.gadgets.model.LigneDistribution;
import com.entreprise.gadgets.model.enums.CategorieBeneficiaire;
import com.entreprise.gadgets.model.enums.TypeDistribution;
import com.entreprise.gadgets.service.PdfGeneratorService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    // Adapter ces trois noms à ceux de des fichiers dans src/main/resources/reports/
    private static final String BORDEREAU_JRXML = "reports/bordereau_dotation.jrxml";
    private static final String ARTICLES_SUB_JRXML = "reports/bordereau_dotation_articles_sub.jrxml";
    private static final String SIGNATAIRES_SUB_JRXML = "reports/bordereau_dotation_signataires_sub.jrxml";

    @Override
    public byte[] genererBordereauPDF(Distribution distribution) {
        try {
            JasperReport principal = compiler(BORDEREAU_JRXML);
            JasperReport sousArticles = compiler(ARTICLES_SUB_JRXML);
            JasperReport sousSignataires = compiler(SIGNATAIRES_SUB_JRXML);

            byte[] logoBytes = chargerLogo();

            List<GroupeBordereau> groupes = construireGroupes(distribution);
            int totalBeneficiaires = groupes.stream().mapToInt(GroupeBordereau::getNombre).sum();
            int totalGadgets = groupes.stream()
                .flatMap(g -> g.getArticles().stream())
                .mapToInt(LigneArticle::getQuantiteTotale).sum();
            int totalChefs = groupes.stream().filter(GroupeBordereau::isChef)
                .mapToInt(GroupeBordereau::getNombre).sum();

            // Signataires : bénéficiaire unique, ou chefs
            boolean beneficiaireUnique = groupes.size() == 1 && groupes.get(0).isSansCategorie();

            List<LigneSignataire> lignesSignataires = new ArrayList<>();
            distribution.getSignataires()
                .forEach(s -> lignesSignataires.add(new LigneSignataire(s.getNomComplet())));

            if (beneficiaireUnique) {
                // Une seule ligne : le nom du bénéficiaire, ou des pointillés s'il n'est pas saisi
                String nom = lignesSignataires.isEmpty() ? "" : lignesSignataires.get(0).getNom();
                lignesSignataires.clear();
                lignesSignataires.add(new LigneSignataire(nom));
            } else {
                // Noms saisis, puis lignes vides (pointillés) jusqu'au nombre de chefs
                for (int i = lignesSignataires.size(); i < totalChefs; i++) {
                    lignesSignataires.add(new LigneSignataire(""));
                }
            }

            // Direction bénéficiaire (à défaut, le service du réceptionniste)
            String structure = distribution.getStructureBeneficiaire();
            if ((structure == null || structure.isBlank())
                    && distribution.getTypeDistribution() == TypeDistribution.INTERNE) {
                structure = distribution.getServiceReceptionnaire();
            }

            Map<String, Object> parametres = new HashMap<>();
            parametres.put("logoStream", new ByteArrayInputStream(logoBytes));
            parametres.put("numeroBordereau", distribution.getNumeroBordereau());
            parametres.put("dateEnvoi", Timestamp.valueOf(
                distribution.getDateDistribution() != null
                    ? distribution.getDateDistribution() : LocalDateTime.now()));
            parametres.put("structureBeneficiaire", structure);
            parametres.put("receptionniste", nomReceptionniste(distribution));
            parametres.put("totalBeneficiaires", totalBeneficiaires);
            parametres.put("totalGadgets", totalGadgets);
            parametres.put("titreBlocSignataires", beneficiaireUnique
                ? "Nom et prénom du bénéficiaire :"
                : "Noms des chefs (en fonction de leur nombre) :");
            parametres.put("afficherBlocChefs", !lignesSignataires.isEmpty());
            parametres.put("sousRapportArticles", sousArticles);
            parametres.put("sousRapportSignataires", sousSignataires);
            parametres.put("signatairesDataSource", new JRBeanCollectionDataSource(lignesSignataires));

            JasperPrint print = JasperFillManager.fillReport(
                principal, parametres, new JRBeanCollectionDataSource(groupes));
            return JasperExportManager.exportReportToPdf(print);

        } catch (BusinessException e) {
            throw e;     // garde le message précis (ex. logo invalide)
        } catch (JRException e) {
            log.error("Erreur JasperReports :", e);
            throw new BusinessException("Erreur lors de la génération du bordereau PDF");
        } catch (Exception e) {
            log.error("Erreur inattendue :", e);
            throw new BusinessException("Erreur lors de la génération du bordereau PDF");
        }
    }
    // ---------- Construction des données ----------

    private List<GroupeBordereau> construireGroupes(Distribution distribution) {
        Map<CategorieBeneficiaire, List<LigneDistribution>> parCategorie = new LinkedHashMap<>();
        distribution.getLignes().stream()
            // On ignore les lignes remplacées par une correction
            .filter(l -> Boolean.TRUE.equals(l.getActif()))
            .sorted(Comparator
                .comparingInt((LigneDistribution l) -> ordre(l.getCategorieBeneficiaire()))
                .thenComparing(LigneDistribution::getIdLigne))
            .forEach(l -> parCategorie
                .computeIfAbsent(l.getCategorieBeneficiaire(), k -> new ArrayList<>()).add(l));

        List<GroupeBordereau> groupes = new ArrayList<>();
        parCategorie.forEach((categorie, lignes) -> groupes.add(new GroupeBordereau(
            categorie != null ? categorie.getLibelle() : "Bénéficiaire",
            categorie != null && categorie.isChef(),
            categorie == null,
            lignes.get(0).getNombreBeneficiaires(),
            lignes.stream()
                .map(l -> new LigneArticle(
                    l.getGadget().getLibelle(),
                    l.getQuantiteParPersonne(),
                    l.getQuantiteDistribuee()))
                .toList())));
        return groupes;
    }

    private int ordre(CategorieBeneficiaire categorie) {
        return categorie == null ? -1 : categorie.ordinal();
    }

    private String nomReceptionniste(Distribution distribution) {
        if (distribution.getTypeDistribution() == TypeDistribution.EXTERNE) {
            return distribution.getDestinataire() != null ? distribution.getDestinataire() : "";
        }
        String prenom = distribution.getPrenomReceptionnaire() == null
            ? "" : distribution.getPrenomReceptionnaire().trim();
        String nom = distribution.getNomReceptionnaire() == null
            ? "" : distribution.getNomReceptionnaire().trim();
        String identite = (prenom + " " + nom).trim();

        List<String> details = new ArrayList<>();
        String matricule = distribution.getMatriculeReceptionnaire();
        String service = distribution.getServiceReceptionnaire();
        if (matricule != null && !matricule.isBlank()) details.add("Mat. " + matricule.trim());
        if (service != null && !service.isBlank()) details.add(service.trim());

        return details.isEmpty() ? identite : identite + " (" + String.join(", ", details) + ")";
    }

    // ---------- Ressources ----------

    private JasperReport compiler(String chemin) throws JRException, IOException {
        try (InputStream in = new ClassPathResource(chemin).getInputStream()) {
            return JasperCompileManager.compileReport(in);
        }
    }

    private byte[] chargerLogo() {
        byte[] logoBytes;
        try (InputStream logoRaw = new ClassPathResource("images/images.png").getInputStream()) {
            logoBytes = logoRaw.readAllBytes();
        } catch (IOException e) {
            throw new BusinessException("Logo introuvable dans resources/images/images.png");
        }

        if (!estUnPngValide(logoBytes)) {
            throw new BusinessException(
                "Le logo n'a pas une signature PNG valide (probablement corrompu par le filtering Maven, "
                + "ou pas vraiment un PNG). Ré-exporte-le proprement (GIMP/Paint) "
                + "et remplace-le dans src/main/resources/images/.");
        }
        return logoBytes;
    }

    private boolean estUnPngValide(byte[] bytes) {
        int[] signature = {0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        if (bytes.length < signature.length) return false;
        for (int i = 0; i < signature.length; i++) {
            if ((bytes[i] & 0xFF) != signature[i]) return false;
        }
        return true;
    }

    // ---------- JavaBeans pour JasperReports (getters obligatoires) ----------

    @Getter @AllArgsConstructor
    public static class LigneArticle {
        private final String libelle;
        private final Integer quantiteParPersonne;
        private final Integer quantiteTotale;
    }

    @Getter @AllArgsConstructor
    public static class GroupeBordereau {
        private final String categorie;
        private final boolean chef;
        private final boolean sansCategorie;
        private final Integer nombre;
        private final List<LigneArticle> articles;
    }

    @Getter @AllArgsConstructor
    public static class LigneSignataire {
        private final String nom;
    }
}