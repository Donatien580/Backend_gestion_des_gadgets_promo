package com.entreprise.gadgets.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record DistributionRequest(
    @NotNull(message = "La demande est obligatoire")
    Integer idDemande,

    LocalDateTime dateDistribution,
    String motif,
    String destinataire
) {}
