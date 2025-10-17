package com.example.druginteractions.adapters.openfda;

import com.example.druginteractions.domain.OpenFdaClient;
import com.example.druginteractions.domain.OpenFdaSignal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenFdaAdapter implements OpenFdaClient {
    private final WebClient openFdaWebClient;

    @Override
    public Mono<OpenFdaSignal> fetchSignals(String drugA, String drugB, int limit) {
        String query = buildQuery(drugA, drugB);
        String aggregationFields = "reactionmeddrapt.exact";

        return openFdaWebClient.get()
            .uri(builder -> builder
                .queryParam("search", query)
                .queryParam("count", aggregationFields)
                .queryParam("limit", limit)
                .build())
            .retrieve()
            .bodyToMono(OpenFdaResponse.class)
            .map(this::mapToSignal)
            .onErrorMap(this::handleError);
    }

    private String buildQuery(String drugA, String drugB) {
        // Case-insensitive search for both drugs in medicinalproduct
        return String.format(
            "patient.drug.medicinalproduct:\"%s\" AND patient.drug.medicinalproduct:\"%s\"",
            escapeQueryValue(drugA),
            escapeQueryValue(drugB)
        );
    }

    private String escapeQueryValue(String value) {
        // Escape special characters in the query value
        return value.replace("\"", "\\\"");
    }

    private OpenFdaSignal mapToSignal(OpenFdaResponse response) {
        if (response.results() == null || response.results().isEmpty()) {
            return new OpenFdaSignal(0, List.of());
        }

        List<Map.Entry<String, Long>> topReactions = response.results().stream()
            .map(result -> new AbstractMap.SimpleEntry<>(
                result.term(),
                result.count()
            ))
            .toList();

        long totalCount = topReactions.stream()
            .mapToLong(Map.Entry::getValue)
            .sum();

        return new OpenFdaSignal(totalCount, topReactions);
    }

    private Throwable handleError(Throwable error) {
        if (error instanceof WebClientResponseException wcre) {
            return switch (HttpStatus.valueOf(wcre.getStatusCode().value())) {
                case TOO_MANY_REQUESTS -> new OpenFdaRateLimitException("OpenFDA API rate limit exceeded", wcre);
                case BAD_REQUEST -> new OpenFdaQueryException("Invalid query parameters", wcre);
                default -> new OpenFdaException("OpenFDA API error: " + wcre.getStatusText(), wcre);
            };
        }
        return new OpenFdaException("Error communicating with OpenFDA API", error);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OpenFdaResponse(List<ResultEntry> results) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        record ResultEntry(String term, long count) {}
    }
}
