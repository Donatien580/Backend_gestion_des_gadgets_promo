package com.entreprise.gadgets.mapper;

import com.entreprise.gadgets.dto.response.InventaireResponse;
import com.entreprise.gadgets.dto.response.LigneInventaireResponse;
import com.entreprise.gadgets.model.Inventaire;
import com.entreprise.gadgets.model.LigneInventaire;
import com.entreprise.gadgets.model.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventaireMapper {

    @Mapping(target = "realisateur", expression = "java(nomComplet(inventaire.getRealisateur()))")
    InventaireResponse versReponse(Inventaire inventaire);

    @Mapping(source = "gadget.idGadget", target = "idGadget")
    @Mapping(source = "gadget.libelle", target = "libelleGadget")
    LigneInventaireResponse versReponse(LigneInventaire ligne);

    default String nomComplet(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }
        return utilisateur.getNom() + " " + utilisateur.getPrenom();
    }
}