package com.entreprise.gadgets.service;

import com.entreprise.gadgets.dto.request.ApprovisionnementRequest;
import com.entreprise.gadgets.dto.response.ApprovisionnementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApprovisionnementService {

    Page<ApprovisionnementResponse> lister(Pageable pageable, String recherche);

    ApprovisionnementResponse obtenirParId(Integer id);

    ApprovisionnementResponse creer(ApprovisionnementRequest requete);
}
