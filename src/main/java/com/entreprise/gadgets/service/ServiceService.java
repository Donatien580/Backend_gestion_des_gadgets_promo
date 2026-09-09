package com.entreprise.gadgets.service;

import com.entreprise.gadgets.dto.request.ServiceRequest;
import com.entreprise.gadgets.dto.response.ServiceResponse;

import java.util.List;

public interface ServiceService {

    List<ServiceResponse> lister();

    ServiceResponse obtenir(Integer id);

    ServiceResponse creer(ServiceRequest requete);

    ServiceResponse modifier(Integer id, ServiceRequest requete);
    
    long compterPersonnelsActifs(Integer id);

    void supprimer(Integer id);
}