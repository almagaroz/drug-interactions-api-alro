package com.example.druginteractions.app.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DrugInteractionRequest(
    @Size(min = 3, max = 60, message = "Drug name must be between 3 and 60 characters")
    @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]{1,58}[A-Za-z]$", message = "Drug name must contain only letters, spaces, and hyphens")
    String drugA,

    @Size(min = 3, max = 60, message = "Drug name must be between 3 and 60 characters")
    @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]{1,58}[A-Za-z]$", message = "Drug name must contain only letters, spaces, and hyphens")
    String drugB,

    @Size(max = 1000, message = "Note must not exceed 1000 characters")
    String note
) {}
