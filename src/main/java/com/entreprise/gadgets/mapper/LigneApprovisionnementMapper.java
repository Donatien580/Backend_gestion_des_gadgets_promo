package com.entreprise.gadgets.mapper;

import com.entreprise.gadgets.dto.response.GadgetResume;
import com.entreprise.gadgets.dto.response.LigneApprovisionnementResponse;
import com.entreprise.gadgets.model.Gadget;
import com.entreprise.gadgets.model.LigneApprovisionnement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LigneApprovisionnementMapper {

    LigneApprovisionnementResponse toResponse(LigneApprovisionnement ligne);

    GadgetResume toResume(Gadget gadget);
}
