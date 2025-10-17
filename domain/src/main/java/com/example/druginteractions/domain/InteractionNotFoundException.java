package com.example.druginteractions.domain;

public class InteractionNotFoundException extends RuntimeException {
    private final DrugPair pair;

    public InteractionNotFoundException(DrugPair pair) {
        super("No interaction found between " + pair.drugA() + " and " + pair.drugB());
        this.pair = pair;
    }

    public DrugPair getDrugPair() {
        return pair;
    }
}
