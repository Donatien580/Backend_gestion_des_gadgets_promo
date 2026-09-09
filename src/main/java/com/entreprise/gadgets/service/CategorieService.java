package com.entreprise.gadgets.service;

import com.entreprise.gadgets.dto.request.CategorieRequest;
import com.entreprise.gadgets.dto.response.CategorieResponse;

import java.util.List;

public interface CategorieService {

    List<CategorieResponse> listerToutes();

    CategorieResponse creer(CategorieRequest requete);

    CategorieResponse modifier(Integer id, CategorieRequest requete);

    void supprimer(Integer id);
}
