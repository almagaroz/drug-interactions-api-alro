package com.example.druginteractions.domain;

import java.time.Instant;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

public record InteractionNote(
    @NonNull UUID id,
    @NonNull @Valid DrugPair pair,
    @NotBlank @Size(max = 1000) String note,
    @NonNull Instant updatedAt
) {
    public InteractionNote {
        if (id == null) id = UUID.randomUUID();
        if (updatedAt == null) updatedAt = Instant.now();
    }
}
