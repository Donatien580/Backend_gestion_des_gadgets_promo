package com.entreprise.gadgets.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.entreprise.gadgets.exception.BusinessException;
import com.entreprise.gadgets.model.Gadget;
import com.entreprise.gadgets.model.MouvementStock;
import com.entreprise.gadgets.model.Utilisateur;
import com.entreprise.gadgets.model.enums.TypeMouvement;
import com.entreprise.gadgets.repository.GadgetRepository;
import com.entreprise.gadgets.repository.MouvementStockRepository;
import com.entreprise.gadgets.service.RapportService;
import com.entreprise.gadgets.service.UtilisateurCourantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Slf4j
@Service
@RequiredArgsConstructor
public class RapportServiceImpl implements RapportService{
	
     private static final String RAPPORT_STOCK_JRXML = "reports/rapport_stock.jrxml";
     private static final String RAPPORT_STOCK_MOUVEMENTS_SUB_JRXML = "reports/rapport_stock_mouvements_sub.jrxml";
     
     private static final String DEPARTEMENT_DEFAUT = "Département Marketing et Innovations";
     
     private final GadgetRepository gadgetRepository;
     private final MouvementStockRepository mouvementStockRepository;
     private final UtilisateurCourantService utilisateurCourantService;

  // ---------- RAPPORT DE STOCK ----------
     @Override
     @Transactional(readOnly = true)
     public byte[] genererRapportStockPdf(LocalDate dateDebut, LocalDate dateFin) {
         if (dateDebut == null || dateFin == null || dateDebut.isAfter(dateFin)) {
             throw new BusinessException("La période demandée est invalide (date de début après date de fin, ou dates manquantes).");
         }

         try {
             LocalDateTime debutPeriode = dateDebut.atStartOfDay();
             LocalDateTime finPeriode = dateFin.atTime(23, 59, 59);

             // Compiler le sous-rapport (mouvements) et le rapport principal
             JasperReport sousRapportMouvements = compilerRapport(RAPPORT_STOCK_MOUVEMENTS_SUB_JRXML);
             JasperReport rapportPrincipal = compilerRapport(RAPPORT_STOCK_JRXML);

             // Logo 
             byte[] logoBytes = chargerLogo();

             // Stock actif, déjà trié par catégorie puis libellé (requis par le group du rapport)
             List<Gadget> gadgets = gadgetRepository.findByActifTrueOrderByCategorie_LibelleAscLibelleAsc();
             List<LigneRapportStock> lignesStock = convertirLignesStock(gadgets);

             int nombreTotalArticles = gadgets.size();
             int articlesEnRupture = (int) gadgets.stream().filter(g -> g.getQuantiteDisponible() == 0).count();
             int articlesEnAlerte = (int) gadgets.stream()
                 .filter(g -> g.getQuantiteDisponible() > 0 && g.getQuantiteDisponible() <= g.getSeuilAlerte())
                 .count();

             // Mouvements de la période
             List<MouvementStock> mouvements = mouvementStockRepository.listerPourRapport(debutPeriode, finPeriode);
             List<LigneRapportMouvement> lignesMouvements = convertirLignesMouvements(mouvements);

             // Paramètres
             Utilisateur utilisateurConnecte = utilisateurCourantService.obtenirUtilisateurConnecte();

             Map<String, Object> parametres = new HashMap<>();
             parametres.put("logoStream", new ByteArrayInputStream(logoBytes));
             parametres.put("dateGeneration", Timestamp.valueOf(LocalDateTime.now()));
             parametres.put("dateDebutPeriode", Timestamp.valueOf(debutPeriode));
             parametres.put("dateFinPeriode", Timestamp.valueOf(finPeriode));
             parametres.put("departement", DEPARTEMENT_DEFAUT);
             parametres.put("genererPar", nomComplet(utilisateurConnecte.getNom(), utilisateurConnecte.getPrenom()));
             parametres.put("nombreTotalArticles", nombreTotalArticles);
             parametres.put("articlesEnAlerte", articlesEnAlerte);
             parametres.put("articlesEnRupture", articlesEnRupture);
             parametres.put("mouvementsSubreport", sousRapportMouvements);
             parametres.put("mouvementsDataSource", new JRBeanCollectionDataSource(lignesMouvements));

             // Remplissage et export
             JRBeanCollectionDataSource dataSourceStock = new JRBeanCollectionDataSource(lignesStock);
             JasperPrint print = JasperFillManager.fillReport(rapportPrincipal, parametres, dataSourceStock);
             return JasperExportManager.exportReportToPdf(print);

         } catch (JRException e) {
             log.error("Erreur JasperReports :", e);
             throw new BusinessException("Erreur lors de la génération du rapport de stock");
         } catch (BusinessException e) {
             throw e;
         } catch (Exception e) {
             log.error("Erreur inattendue :", e);
             throw new BusinessException("Erreur lors de la génération du rapport de stock");
         }
     }

     // ================== MÉTHODES PRIVÉES ==================

     private JasperReport compilerRapport(String cheminClasspath) throws JRException, IOException {
         try (InputStream jrxmlStream = new ClassPathResource(cheminClasspath).getInputStream()) {
             return JasperCompileManager.compileReport(jrxmlStream);
         }
     }

     /** Même chargement/validation du logo */
     private byte[] chargerLogo() {
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

     private List<LigneRapportStock> convertirLignesStock(List<Gadget> gadgets) {
         List<LigneRapportStock> lignes = new ArrayList<>();
         for (Gadget gadget : gadgets) {
             lignes.add(new LigneRapportStock(
                 gadget.getLibelle(),
                 gadget.getDesignation(),
                 gadget.getCategorie().getLibelle(),
                 gadget.getQuantiteDisponible(),
                 gadget.getSeuilAlerte()
             ));
         }
         return lignes;
     }

     private List<LigneRapportMouvement> convertirLignesMouvements(List<MouvementStock> mouvements) {
         List<LigneRapportMouvement> lignes = new ArrayList<>();
         for (MouvementStock mouvement : mouvements) {
             Utilisateur auteur = mouvement.getUtilisateur();
             lignes.add(new LigneRapportMouvement(
                 Timestamp.valueOf(mouvement.getDateMouvement()),
                 mouvement.getGadget().getLibelle(),
                 libelleTypeMouvement(mouvement.getTypeMouvement()),
                 quantiteSignee(mouvement),
                 auteur != null ? nomComplet(auteur.getNom(), auteur.getPrenom()) : "—",
                	formaterReference(mouvement),	 
                 mouvement.getMotif()
             ));
         }
         return lignes;
     }
     
     private String formaterReference(MouvementStock mouvement) {
    	    String typeRef = mouvement.getTypeReference();
    	    if (typeRef == null || typeRef.isBlank()) return "—";
    	    // On garde le libellé tel quel, ex: "APPROVISIONNEMENT", "DISTRIBUTION"
    	    return typeRef;
    	}
     
     private Integer quantiteSignee(MouvementStock mouvement) {
 	    return switch (mouvement.getTypeMouvement()) {
 	        case INVENTAIRE, CORRECTION -> mouvement.getStockApres() - mouvement.getStockAvant();
 	        default -> mouvement.getQuantite();
 	    };
 	}

     private String libelleTypeMouvement(TypeMouvement type) {
         if (type == null) return "—";
         return switch (type) {
             case ENTREE -> "Entrée";
             case SORTIE -> "Sortie";
             case RETOUR -> "Retour";
             case INVENTAIRE -> "Inventaire";
             case CORRECTION -> "Correction";
         };
     }

     private String nomComplet(String nom, String prenom) {
         if (nom == null) return "";
         return (prenom != null && !prenom.isBlank()) ? prenom + " " + nom : nom;
     }

     // ================== JAVABEANS POUR JASPERREPORTS ==================
     // une classe simple par ligne de tableau.

     public static class LigneRapportStock {
         private final String libelle;
         private final String designation;
         private final String categorie;
         private final Integer stock;
         private final Integer seuil;

         public LigneRapportStock(String libelle, String designation, String categorie, Integer stock, Integer seuil) {
             this.libelle = libelle;
             this.designation = designation;
             this.categorie = categorie;
             this.stock = stock;
             this.seuil = seuil;
         }

         public String getLibelle() { return libelle; }
         public String getDesignation() { return designation; }
         public String getCategorie() { return categorie; }
         public Integer getStock() { return stock; }
         public Integer getSeuil() { return seuil; }
     }

     public static class LigneRapportMouvement {
         private final Timestamp dateMouvement;
         private final String article;
         private final String typeMouvement;
         private final Integer quantite;
         private final String utilisateur;
         private final String reference;
         private final String motif;

         public LigneRapportMouvement(Timestamp dateMouvement, String article, String typeMouvement,
                                       Integer quantite, String utilisateur, String reference, String motif) {
             this.dateMouvement = dateMouvement;
             this.article = article;
             this.typeMouvement = typeMouvement;
             this.quantite = quantite;
             this.utilisateur = utilisateur;
             this.reference = reference;
             this.motif = motif;
         }

         public Timestamp getDateMouvement() { return dateMouvement; }
         public String getArticle() { return article; }
         public String getTypeMouvement() { return typeMouvement; }
         public Integer getQuantite() { return quantite; }
         public String getUtilisateur() { return utilisateur; }
         public String getReference() { return reference;}
         public String getMotif() { return motif; }
     }
 
}
