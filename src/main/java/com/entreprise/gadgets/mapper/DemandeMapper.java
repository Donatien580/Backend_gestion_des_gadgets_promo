package com.entreprise.gadgets.mapper;

import com.entreprise.gadgets.dto.response.*;
import com.entreprise.gadgets.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DemandeMapper {

	public DemandeResponse toResponse(Demande demande) {
        return new DemandeResponse(
            demande.getIdDemande(),
            demande.getNumeroDemande(),
            demande.getObjet(),
            demande.getTypeDemande(),
            demande.getDateDemande(),
            demande.getDateSouhaitee(),
            demande.getDateValidation(),
            demande.getDateTraitement(),
            demande.getEtat(),
            demande.getMotifRefus(),
            demande.getObservations(),
            demande.getNomDemandeur(),
            demande.getPrenomDemandeur(),
            demande.getTelephoneDemandeur(),
            demande.getMatriculeDemandeur(),
            demande.getServiceDemandeur(),
            demande.getStructureDemandeur(),
            toAgentResume(demande.getAgentSaisie()),
            toAgentResume(demande.getAgentAffecte()),
            toPieceResponse(demande.getPieceJustificative())
        );
    }

    private AgentResume toAgentResume(Utilisateur utilisateur) {
        if (utilisateur == null) return null;
        return new AgentResume(utilisateur.getIdUtilisateur(), utilisateur.getNom(), utilisateur.getPrenom());
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