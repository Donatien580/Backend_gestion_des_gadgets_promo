package com.entreprise.gadgets.mapper;

import com.entreprise.gadgets.dto.response.*;
import com.entreprise.gadgets.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DemandeMapper {

    public DemandeResponse toResponse(Demande demande) {
        List<LigneDemandeResponse> lignes = demande.getLignes()
                .stream()
                .map(this::toLigneResponse)
                .toList();

        PieceJustificativeResponse piece = toPieceResponse(demande.getPieceJustificative());

        // Informations du service pour demande interne
        Integer idService = null;
        String libelleService = null;
        String matriculeResponsable = null;
        String nomResponsable = null;
        
        if (demande.getService() != null) {
            Services service = demande.getService();
            idService = service.getIdService();
            libelleService = service.getLibelleService();
            matriculeResponsable = service.getMatriculeResponsable();
            nomResponsable = service.getNomResponsable();
        }

        return new DemandeResponse(
                demande.getIdDemande(),
                demande.getNumeroDemande(),
                demande.getObjet(),
                demande.getTypeDemande(),
                demande.getDateDemande(),
                demande.getDateSouhaitee(),
                demande.getDateValidation(),
                demande.getEtat(),
                demande.getMotifRefus(),
                demande.getObservations(),
                idService,
                libelleService,
                matriculeResponsable,
                nomResponsable,
                demande.getNombrePersonnelsImpactes(),
                demande.getStructure(),
                demande.getRepresentant(),
                demande.getTelephone(),
                demande.getAgentAffecte() != null ? demande.getAgentAffecte().getIdUtilisateur() : null,
                demande.getAgentAffecte() != null
                        ? demande.getAgentAffecte().getNom() + " " + demande.getAgentAffecte().getPrenom()
                        : null,
                piece,
                lignes
        );
    }

    private LigneDemandeResponse toLigneResponse(LigneDemande ligne) {
        return new LigneDemandeResponse(
                ligne.getIdLigne(),
                ligne.getGadget().getIdGadget(),
                ligne.getGadget().getLibelle(),
                ligne.getQuantiteDemandee(),
                ligne.getQuantiteAccordee()
        );
    }

    private PieceJustificativeResponse toPieceResponse(PieceJustificative piece) {
        if (piece == null) return null;
        return new PieceJustificativeResponse(
                piece.getNomFichier(),
                piece.getTypeFichier(),
                piece.getCheminFichier()
        );
    }
}