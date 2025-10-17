package com.example.druginteractions.domain;

import reactor.core.publisher.Mono;

public interface OpenFdaClient {
    Mono<OpenFdaSignal> fetchSignals(String drugA, String drugB, int limit);
}
