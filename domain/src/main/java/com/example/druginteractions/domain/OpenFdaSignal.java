package com.example.druginteractions.domain;

import java.util.List;
import java.util.Map.Entry;
import reactor.core.publisher.Mono;

public record OpenFdaSignal(
    long count,
    List<Entry<String, Long>> topReactions
) {}
