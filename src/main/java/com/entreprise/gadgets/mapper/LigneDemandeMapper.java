package com.entreprise.gadgets.mapper;

import com.entreprise.gadgets.dto.response.LigneDemandeResponse;
import com.entreprise.gadgets.model.LigneDemande;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = LigneApprovisionnementMapper.class)
public interface LigneDemandeMapper {
    LigneDemandeResponse toResponse(LigneDemande ligne);
}
