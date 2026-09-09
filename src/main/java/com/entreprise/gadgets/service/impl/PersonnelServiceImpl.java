package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.dto.request.PersonnelRequest;
import com.entreprise.gadgets.dto.response.PersonnelResponse;
import com.entreprise.gadgets.exception.BusinessException;
import com.entreprise.gadgets.exception.RessourceIntrouvableException;
import com.entreprise.gadgets.mapper.PersonnelMapper;
import com.entreprise.gadgets.model.Personnel;
import com.entreprise.gadgets.model.Services;
import com.entreprise.gadgets.repository.PersonnelRepository;
import com.entreprise.gadgets.repository.ServiceDemandeurRepository;
import com.entreprise.gadgets.service.PersonnelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonnelServiceImpl implements PersonnelService {

    private final PersonnelRepository personnelRepository;
    private final ServiceDemandeurRepository serviceRepository;
    private final PersonnelMapper personnelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PersonnelResponse> lister() {
        return personnelRepository.findAll()
                .stream()
                .map(personnelMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PersonnelResponse> listerActifsParService(Integer idService) {
        return personnelRepository.findByService_IdServiceAndActifTrue(idService)
                .stream()
                .map(personnelMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PersonnelResponse obtenir(Integer id) {
        Personnel personnel = personnelRepository.findByIdWithService(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Personnel introuvable"));
        return personnelMapper.toResponse(personnel);
    }

    @Override
    public PersonnelResponse creer(PersonnelRequest requete) {
        if (personnelRepository.existsByMatricule(requete.matricule())) {
            throw new BusinessException("Le matricule existe déjà");
        }
        Services service = serviceRepository.findById(requete.idService())
                .orElseThrow(() -> new RessourceIntrouvableException("Service introuvable"));

        Personnel personnel = Personnel.builder()
                .nom(requete.nom())
                .prenom(requete.prenom())
                .matricule(requete.matricule())
                .telephone(requete.telephone())
                .fonction(requete.fonction())
                .actif(requete.actif() != null ? requete.actif() : true)
                .service(service)
                .build();
        Personnel saved = personnelRepository.save(personnel);
        return personnelMapper.toResponse(saved);
    }

    @Override
    public PersonnelResponse modifier(Integer id, PersonnelRequest requete) {
        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Personnel introuvable"));
        if (!personnel.getMatricule().equals(requete.matricule()) &&
                personnelRepository.existsByMatricule(requete.matricule())) {
            throw new BusinessException("Le matricule existe déjà");
        }
        Services service = serviceRepository.findById(requete.idService())
                .orElseThrow(() -> new RessourceIntrouvableException("Service introuvable"));

        personnel.setNom(requete.nom());
        personnel.setPrenom(requete.prenom());
        personnel.setMatricule(requete.matricule());
        personnel.setTelephone(requete.telephone());
        personnel.setFonction(requete.fonction());
        personnel.setActif(requete.actif() != null ? requete.actif() : true);
        personnel.setService(service);

        Personnel saved = personnelRepository.save(personnel);
        return personnelMapper.toResponse(saved);
    }

    @Override
    public void supprimer(Integer id) {
        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Personnel introuvable"));
        // Suppression logique : désactiver le personnel
        personnel.setActif(false);
        personnelRepository.save(personnel);
    }
}