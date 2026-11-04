package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.dto.request.ModificationUtilisateurRequest;

import com.entreprise.gadgets.dto.request.UtilisateurRequest;
import com.entreprise.gadgets.dto.response.PageResponse;
import com.entreprise.gadgets.dto.response.UtilisateurResponse;
import com.entreprise.gadgets.exception.BusinessException;
import com.entreprise.gadgets.exception.RessourceIntrouvableException;
import com.entreprise.gadgets.mapper.UtilisateurMapper;
import com.entreprise.gadgets.model.Role;
import com.entreprise.gadgets.model.Utilisateur;
import com.entreprise.gadgets.model.enums.RoleType;
import com.entreprise.gadgets.repository.RoleRepository;
import com.entreprise.gadgets.repository.UtilisateurRepository;
import com.entreprise.gadgets.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final UtilisateurMapper utilisateurMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UtilisateurResponse> lister(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        return PageResponse.from(utilisateurRepository.findAll(pageable).map(utilisateurMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponse obtenir(Integer id) {
        return utilisateurMapper.toResponse(trouverParId(id));
    }

    @Override
    public UtilisateurResponse creer(UtilisateurRequest requete) {
        if (utilisateurRepository.existsByEmail(requete.email())) {
            throw new BusinessException("Un utilisateur avec l'email \"" + requete.email() + "\" existe déjà.");
        }

        Utilisateur utilisateur = Utilisateur.builder()
            .nom(requete.nom())
            .prenom(requete.prenom())
            .email(requete.email())
            .actif(true)
            .role(trouverRole(requete.role()))
            .build();

        return utilisateurMapper.toResponse(utilisateurRepository.save(utilisateur));
    }

    @Override
    public UtilisateurResponse modifier(Integer id, ModificationUtilisateurRequest requete) {
        Utilisateur utilisateur = trouverParId(id);

        utilisateurRepository.findByEmail(requete.email()).ifPresent(existant -> {
            if (!existant.getIdUtilisateur().equals(id)) {
                throw new BusinessException("Un autre utilisateur utilise déjà l'email \"" + requete.email() + "\".");
            }
        });

        utilisateur.setNom(requete.nom());
        utilisateur.setPrenom(requete.prenom());
        utilisateur.setEmail(requete.email());
        utilisateur.setActif(requete.actif());
        utilisateur.setRole(trouverRole(requete.role()));

        return utilisateurMapper.toResponse(utilisateurRepository.save(utilisateur));
    }

    @Override
    public UtilisateurResponse changerStatutActif(Integer id, boolean actif) {
        Utilisateur utilisateur = trouverParId(id);
        utilisateur.setActif(actif);
        return utilisateurMapper.toResponse(utilisateurRepository.save(utilisateur));
    }

    private Role trouverRole(RoleType roleType) {
        return roleRepository.findByNom(roleType)
            .orElseThrow(() -> new IllegalStateException("Le rôle \"" + roleType + "\" n'existe pas en base."));
    }

    private Utilisateur trouverParId(Integer id) {
        return utilisateurRepository.findById(id)
            .orElseThrow(() -> RessourceIntrouvableException.pour("Utilisateur", id));
    }
}