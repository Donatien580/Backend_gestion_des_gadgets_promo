package com.entreprise.gadgets.service;

import com.entreprise.gadgets.dto.request.ModificationUtilisateurRequest;
import com.entreprise.gadgets.dto.request.UtilisateurRequest;
import com.entreprise.gadgets.dto.response.PageResponse;
import com.entreprise.gadgets.dto.response.UtilisateurResponse;

public interface UtilisateurService {
	PageResponse<UtilisateurResponse> lister(int page, int size);
	UtilisateurResponse obtenir(Integer id);
	UtilisateurResponse creer(UtilisateurRequest requete);
	UtilisateurResponse modifier(Integer id, ModificationUtilisateurRequest requete);
	UtilisateurResponse changerStatutActif(Integer id, boolean actif);

}
