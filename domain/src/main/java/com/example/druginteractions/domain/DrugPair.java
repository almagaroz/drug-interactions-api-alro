package com.example.druginteractions.domain;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

public record DrugPair(
    @NonNull
    @Size(min = 3, max = 60)
    @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]{1,58}[A-Za-z]$")
    String drugA,

    @NonNull
    @Size(min = 3, max = 60)
    @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]{1,58}[A-Za-z]$")
    String drugB
) {
    public DrugPair {
        // Normalize drug names by trimming and converting to title case
        drugA = normalizeDrugName(drugA);
        drugB = normalizeDrugName(drugB);

        // Ensure consistent ordering of drug pairs
        if (drugA.compareToIgnoreCase(drugB) > 0) {
            String temp = drugA;
            drugA = drugB;
            drugB = temp;
        }
    }

    private static String normalizeDrugName(String name) {
        if (name == null) throw new IllegalArgumentException("Drug name cannot be null");

        String trimmed = name.trim();
        if (trimmed.isEmpty()) throw new IllegalArgumentException("Drug name cannot be empty");

        // Convert to title case
        String[] words = trimmed.toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                if (result.length() > 0) result.append(" ");
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1));
            }
        }

        return result.toString();
    }
}
