package com.entreprise.gadgets.service;

import com.entreprise.gadgets.dto.request.PersonnelRequest;
import com.entreprise.gadgets.dto.response.PersonnelResponse;

import java.util.List;

public interface PersonnelService {

    List<PersonnelResponse> lister();
    
    List<PersonnelResponse> listerActifsParService(Integer idService);

    PersonnelResponse obtenir(Integer id);

    PersonnelResponse creer(PersonnelRequest requete);

    PersonnelResponse modifier(Integer id, PersonnelRequest requete);

    void supprimer(Integer id); 
}