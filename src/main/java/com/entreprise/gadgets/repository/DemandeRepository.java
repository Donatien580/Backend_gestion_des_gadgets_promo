/*package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.Demande;
import com.entreprise.gadgets.model.enums.EtatDemande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DemandeRepository extends JpaRepository<Demande, Integer> {

    Optional<Demande> findByNumeroDemande(String numeroDemande);

    Page<Demande> findByEtat(EtatDemande etat, Pageable pageable);

    Page<Demande> findByAgentAffecte_IdUtilisateur(Integer idAgentAffecte, Pageable pageable);

    Page<Demande> findByAgentSaisie_IdUtilisateur(Integer idAgentSaisie, Pageable pageable);

    @Query("SELECT d FROM Demande d WHERE (:etat IS NULL OR d.etat = :etat) AND (:recherche IS NULL OR LOWER(d.numeroDemande) LIKE LOWER(CONCAT('%', CAST(:recherche AS string), '%')) OR LOWER(d.objet) LIKE LOWER(CONCAT('%', CAST(:recherche AS string), '%'))) ORDER BY d.dateDemande DESC")
    Page<Demande> rechercher(EtatDemande etat, String recherche, Pageable pageable);

    // Alimente la notification de rappel (US-24) : demandes en attente depuis plus de `depuis`.
    @Query("SELECT d FROM Demande d WHERE d.etat = com.entreprise.gadgets.model.enums.EtatDemande.EN_ATTENTE AND d.dateDemande <= :depuis")
    List<Demande> findEnAttenteDepuis(LocalDateTime depuis);
}*/

package com.entreprise.gadgets.repository;

import com.entreprise.gadgets.model.Demande;
import com.entreprise.gadgets.model.enums.EtatDemande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DemandeRepository extends JpaRepository<Demande, Integer> {

    boolean existsByNumeroDemande(String numeroDemande);

    // Pagination simple sans filtre
    Page<Demande> findAll(Pageable pageable);

    // Filtre par état uniquement
    Page<Demande> findByEtat(EtatDemande etat, Pageable pageable);

    // Recherche uniquement (numéro ou objet)
    @Query("SELECT d FROM Demande d WHERE LOWER(d.numeroDemande) LIKE LOWER(CONCAT('%', :recherche, '%')) " +
           "OR LOWER(d.objet) LIKE LOWER(CONCAT('%', :recherche, '%'))")
    Page<Demande> rechercher(@Param("recherche") String recherche, Pageable pageable);

    // Filtre + recherche
    @Query("SELECT d FROM Demande d WHERE d.etat = :etat AND " +
           "(LOWER(d.numeroDemande) LIKE LOWER(CONCAT('%', :recherche, '%')) " +
           "OR LOWER(d.objet) LIKE LOWER(CONCAT('%', :recherche, '%')))")
    Page<Demande> findByEtatAndRecherche(@Param("etat") EtatDemande etat,
                                         @Param("recherche") String recherche,
                                         Pageable pageable);
}