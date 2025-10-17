package com.example.druginteractions.domain;

import java.util.Optional;

public interface InteractionRepository {
    Optional<InteractionNote> find(DrugPair pair);
    InteractionNote upsert(InteractionNote note);
}
