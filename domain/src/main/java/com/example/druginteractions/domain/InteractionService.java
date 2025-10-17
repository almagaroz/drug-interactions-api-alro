package com.example.druginteractions.domain;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

@Service
@Validated
@RequiredArgsConstructor
public class InteractionService {
    private final InteractionRepository repository;
    private final OpenFdaClient openFdaClient;

    public InteractionNote upsertInteraction(@Valid DrugPair pair, String note) {
        var interaction = new InteractionNote(
            null, // UUID will be auto-generated
            pair,
            note,
            null  // timestamp will be auto-set
        );
        return repository.upsert(interaction);
    }

    public InteractionNote findInteraction(@Valid DrugPair pair) {
        return repository.find(pair)
            .orElseThrow(() -> new InteractionNotFoundException(pair));
    }

    public Mono<OpenFdaSignal> getAdverseEventSignals(@Valid DrugPair pair, int limit) {
        if (limit <= 0 || limit > 50) {
            limit = 50; // Default/max limit
        }
        return openFdaClient.fetchSignals(pair.drugA(), pair.drugB(), limit);
    }
}
