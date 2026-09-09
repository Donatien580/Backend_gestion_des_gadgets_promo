package com.entreprise.gadgets.mapper;

import com.entreprise.gadgets.dto.response.PersonnelResponse;
import com.entreprise.gadgets.model.Personnel;
import org.springframework.stereotype.Component;

@Component
public class PersonnelMapper {

    public PersonnelResponse toResponse(Personnel personnel) {
        return new PersonnelResponse(
                personnel.getIdPersonnel(),
                personnel.getNom(),
                personnel.getPrenom(),
                personnel.getMatricule(),
                personnel.getTelephone(),
                personnel.getFonction(),
                personnel.getActif(),
                personnel.getService() != null ? personnel.getService().getIdService() : null,
                personnel.getService() != null ? personnel.getService().getLibelleService() : null
        );
    }
}