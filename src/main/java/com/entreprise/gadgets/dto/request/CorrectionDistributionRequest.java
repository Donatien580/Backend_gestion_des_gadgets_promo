package com.entreprise.gadgets.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record CorrectionDistributionRequest(
    @NotBlank String motifCorrection,
    String destinataire,
    String matriculeReceptionnaire,
    String nomReceptionnaire,
    String prenomReceptionnaire,
    String serviceReceptionnaire,
    @Size(max = 150) String structureBeneficiaire,
    LocalDateTime dateDistribution,
    String motif,
    @NotEmpty @Valid List<GroupeBeneficiaireRequest> groupes
    
) {}