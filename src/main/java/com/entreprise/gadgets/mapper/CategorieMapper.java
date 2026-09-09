package com.entreprise.gadgets.mapper;

import com.entreprise.gadgets.dto.request.CategorieRequest;
import com.entreprise.gadgets.dto.response.CategorieResponse;
import com.entreprise.gadgets.model.Categorie;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategorieMapper {

    CategorieResponse toResponse(Categorie categorie);

    Categorie toEntity(CategorieRequest requete);

    /** Met à jour une catégorie existante (entité déjà chargée) à partir du DTO de saisie. */
    void mettreAJour(CategorieRequest requete, @MappingTarget Categorie categorie);
}
