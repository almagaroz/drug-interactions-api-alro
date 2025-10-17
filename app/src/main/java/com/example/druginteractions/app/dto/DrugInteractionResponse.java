package com.example.druginteractions.app.dto;

import java.time.Instant;
import java.util.UUID;

public record DrugInteractionResponse(
    UUID id,
    String drugA,
    String drugB,
    String note,
    Instant updatedAt
) {}
