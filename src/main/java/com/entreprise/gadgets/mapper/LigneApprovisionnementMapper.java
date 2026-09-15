package com.entreprise.gadgets.mapper;

import com.entreprise.gadgets.dto.response.GadgetResume;

import com.entreprise.gadgets.dto.response.LigneApprovisionnementResponse;
import com.entreprise.gadgets.model.Gadget;
import com.entreprise.gadgets.model.LigneApprovisionnement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LigneApprovisionnementMapper {

	@Mapping(target = "idLigneRemplacee", source = "ligneRemplacee.idLigne")
    LigneApprovisionnementResponse toResponse(LigneApprovisionnement ligne);

    GadgetResume toResume(Gadget gadget);
}
