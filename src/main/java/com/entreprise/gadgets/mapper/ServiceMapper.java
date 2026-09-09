package com.entreprise.gadgets.mapper;

import com.entreprise.gadgets.dto.response.PersonnelResponse;
import com.entreprise.gadgets.dto.response.ServiceResponse;
import com.entreprise.gadgets.model.Personnel;
import com.entreprise.gadgets.model.Services;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceMapper {

    private final PersonnelMapper personnelMapper;

    public ServiceMapper(PersonnelMapper personnelMapper) {
        this.personnelMapper = personnelMapper;
    }

    public ServiceResponse toResponse(Services service) {
        List<PersonnelResponse> personnels = service.getPersonnels()
                .stream()
                .map(personnelMapper::toResponse)
                .toList();
        return new ServiceResponse(
                service.getIdService(),
                service.getLibelleService(),
                service.getCodeService(),
                service.getMatriculeResponsable(),
                service.getNomResponsable(),
                service.getTelephoneResponsable(),
                personnels
        );
    }
}