package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.dto.request.ServiceRequest;
import com.entreprise.gadgets.dto.response.ServiceResponse;
import com.entreprise.gadgets.exception.BusinessException;
import com.entreprise.gadgets.exception.RessourceIntrouvableException;
import com.entreprise.gadgets.mapper.ServiceMapper;
import com.entreprise.gadgets.model.Services;
import com.entreprise.gadgets.repository.PersonnelRepository;
import com.entreprise.gadgets.repository.ServiceDemandeurRepository;
import com.entreprise.gadgets.service.ServiceService;
import lombok.RequiredArgsConstructor; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceServiceImpl implements ServiceService {

    private final ServiceDemandeurRepository serviceRepository;
    private final ServiceMapper serviceMapper;
    private final PersonnelRepository personnelRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponse> lister() {
        return serviceRepository.findAll()
                .stream()
                .map(serviceMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long compterPersonnelsActifs(Integer id) {
        return personnelRepository.countByService_IdServiceAndActifTrue(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse obtenir(Integer id) {
        Services service = serviceRepository.findByIdWithPersonnels(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Service introuvable"));
        return serviceMapper.toResponse(service);
    }

    @Override
    public ServiceResponse creer(ServiceRequest requete) {
        if (serviceRepository.existsByCodeService(requete.codeService())) {
            throw new BusinessException("Le code service existe déjà");
        }
        Services service = Services.builder()
                .libelleService(requete.libelleService())
                .codeService(requete.codeService())
                .matriculeResponsable(requete.matriculeResponsable())
                .nomResponsable(requete.nomResponsable())
                .telephoneResponsable(requete.telephoneResponsable())
                .build();
        Services saved = serviceRepository.save(service);
        return serviceMapper.toResponse(saved);
    }

    @Override
    public ServiceResponse modifier(Integer id, ServiceRequest requete) {
        Services service = serviceRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Service introuvable"));
        if (!service.getCodeService().equals(requete.codeService()) &&
                serviceRepository.existsByCodeService(requete.codeService())) {
            throw new BusinessException("Le code service existe déjà");
        }
        service.setLibelleService(requete.libelleService());
        service.setCodeService(requete.codeService());
        service.setMatriculeResponsable(requete.matriculeResponsable());
        service.setNomResponsable(requete.nomResponsable());
        service.setTelephoneResponsable(requete.telephoneResponsable());
        Services saved = serviceRepository.save(service);
        return serviceMapper.toResponse(saved);
    }

    @Override
    public void supprimer(Integer id) {
        Services service = serviceRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Service introuvable"));

        if (!service.getPersonnels().isEmpty()) {
            throw new BusinessException("Impossible de supprimer un service ayant des personnels associés");
        }
        serviceRepository.delete(service);
    }
}