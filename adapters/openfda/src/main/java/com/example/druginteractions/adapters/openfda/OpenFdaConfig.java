package com.example.druginteractions.adapters.openfda;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class OpenFdaConfig {
    @Value("${openfda.api.key:}")
    private String apiKey;

    @Value("${openfda.api.timeout:10}")
    private int timeoutSeconds;

    @Bean
    public WebClient openFdaWebClient() {
        HttpClient httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(timeoutSeconds))
            .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(timeoutSeconds, TimeUnit.SECONDS))
                .addHandlerLast(new WriteTimeoutHandler(timeoutSeconds, TimeUnit.SECONDS)));

        return WebClient.builder()
            .baseUrl("https://api.fda.gov/drug/event.json")
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .defaultHeader("Accept", "application/json")
            .filter((request, next) -> {
                if (!apiKey.isEmpty()) {
                    return next.exchange(request.mutate()
                        .uri(builder -> builder.queryParam("api_key", apiKey).build())
                        .build());
                }
                return next.exchange(request);
            })
            .build();
    }
}
