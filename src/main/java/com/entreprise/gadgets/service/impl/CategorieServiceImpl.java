package com.entreprise.gadgets.service.impl;

import com.entreprise.gadgets.dto.request.CategorieRequest;
import com.entreprise.gadgets.dto.response.CategorieResponse;
import com.entreprise.gadgets.exception.BusinessException;
import com.entreprise.gadgets.exception.RessourceIntrouvableException;
import com.entreprise.gadgets.mapper.CategorieMapper;
import com.entreprise.gadgets.model.Categorie;
import com.entreprise.gadgets.repository.CategorieRepository;
import com.entreprise.gadgets.repository.GadgetRepository;
import com.entreprise.gadgets.service.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorieServiceImpl implements CategorieService {

    private final CategorieRepository categorieRepository;
    private final GadgetRepository gadgetRepository;
    private final CategorieMapper categorieMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategorieResponse> listerToutes() {
        return categorieRepository.findAll().stream()
            .map(categorieMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public CategorieResponse creer(CategorieRequest requete) {
        Categorie categorie = categorieMapper.toEntity(requete);
        return categorieMapper.toResponse(categorieRepository.save(categorie));
    }

    @Override
    @Transactional
    public CategorieResponse modifier(Integer id, CategorieRequest requete) {
        Categorie categorie = trouverParId(id);
        categorieMapper.mettreAJour(requete, categorie);
  
        return categorieMapper.toResponse(categorie);
    }

    @Override
    @Transactional
    public void supprimer(Integer id) {
        Categorie categorie = trouverParId(id);

        if (gadgetRepository.existsByCategorie_IdCategorie(id)) {
            throw new BusinessException(
                "Impossible de supprimer la catégorie \"" + categorie.getLibelle()
                    + "\" : elle est encore utilisée par au moins un gadget.");
        }

        categorieRepository.delete(categorie);
    }

    private Categorie trouverParId(Integer id) {
        return categorieRepository.findById(id)
            .orElseThrow(() -> RessourceIntrouvableException.pour("Catégorie", id));
    }
}
