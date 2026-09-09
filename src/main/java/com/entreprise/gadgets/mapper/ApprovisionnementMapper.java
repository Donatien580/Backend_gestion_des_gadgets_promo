package com.entreprise.gadgets.mapper;

import com.entreprise.gadgets.dto.response.ApprovisionnementResponse;
import com.entreprise.gadgets.model.Approvisionnement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { LigneApprovisionnementMapper.class, IncidentMapper.class })
public interface ApprovisionnementMapper {
    ApprovisionnementResponse toResponse(Approvisionnement approvisionnement);
}
