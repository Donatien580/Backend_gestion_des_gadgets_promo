package com.entreprise.gadgets.mapper;

import com.entreprise.gadgets.dto.response.IncidentResponse;
import com.entreprise.gadgets.model.Incident;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IncidentMapper {
    IncidentResponse toResponse(Incident incident);
}
