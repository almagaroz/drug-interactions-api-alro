package com.example.druginteractions.app.controller;

import com.example.druginteractions.app.dto.DrugInteractionRequest;
import com.example.druginteractions.app.dto.DrugInteractionResponse;
import com.example.druginteractions.domain.DrugPair;
import com.example.druginteractions.domain.InteractionNote;
import com.example.druginteractions.domain.InteractionService;
import com.example.druginteractions.domain.OpenFdaSignal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
@Validated
@RequiredArgsConstructor
public class DrugInteractionController {
    private final InteractionService interactionService;

    @PostMapping("/interactions")
    public ResponseEntity<DrugInteractionResponse> createInteraction(
            @RequestBody @Valid DrugInteractionRequest request
    ) {
        var pair = new DrugPair(request.drugA(), request.drugB());
        var note = interactionService.upsertInteraction(pair, request.note());
        return ResponseEntity.ok(toResponse(note));
    }

    @GetMapping("/interactions")
    public ResponseEntity<DrugInteractionResponse> getInteraction(
            @RequestParam @Size(min = 3, max = 60)
            @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]{1,58}[A-Za-z]$") String drugA,
            @RequestParam @Size(min = 3, max = 60)
            @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]{1,58}[A-Za-z]$") String drugB
    ) {
        var pair = new DrugPair(drugA, drugB);
        var note = interactionService.findInteraction(pair);
        return ResponseEntity.ok(toResponse(note));
    }

    @GetMapping("/signals")
    public Mono<ResponseEntity<OpenFdaSignal>> getSignals(
            @RequestParam @Size(min = 3, max = 60)
            @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]{1,58}[A-Za-z]$",
                    message = "Drug name must contain only letters, spaces, and hyphens") String drugA,
            @RequestParam @Size(min = 3, max = 60)
            @Pattern(regexp = "^[A-Za-z][A-Za-z\\s-]{1,58}[A-Za-z]$",
                    message = "Drug name must contain only letters, spaces, and hyphens") String drugB,
            @RequestParam(defaultValue = "50") @Min(1) @Max(50) int limit
    ) {
        var pair = new DrugPair(drugA, drugB);
        return interactionService.getAdverseEventSignals(pair, limit)
                .map(ResponseEntity::ok)
                .onErrorMap(e -> {
                    // Let the global exception handler deal with the mapped exceptions
                    throw e;
                });
    }

    private DrugInteractionResponse toResponse(InteractionNote note) {
        return new DrugInteractionResponse(
            note.id(),
            note.pair().drugA(),
            note.pair().drugB(),
            note.note(),
            note.updatedAt()
        );
    }
}
