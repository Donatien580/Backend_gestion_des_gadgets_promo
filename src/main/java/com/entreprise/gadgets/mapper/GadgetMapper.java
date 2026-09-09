package com.entreprise.gadgets.mapper;

import com.entreprise.gadgets.dto.response.GadgetResponse;
import com.entreprise.gadgets.model.Gadget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Ne mappe volontairement pas GadgetRequest -> Gadget : la création/mise à
 * jour d'un gadget nécessite de résoudre la Catégorie associée et d'imposer
 * des valeurs par défaut (stock à 0, actif à true), ce qui relève de
 * GadgetService plutôt que d'un mapping automatique.
 */
@Mapper(componentModel = "spring", uses = CategorieMapper.class)
public interface GadgetMapper {

    @Mapping(target = "sousSeuilAlerte", expression = "java(gadget.estSousSeuilAlerte())")
    GadgetResponse toResponse(Gadget gadget);
}
