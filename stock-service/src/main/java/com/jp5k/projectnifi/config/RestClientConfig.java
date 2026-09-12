package com.jp5k.projectnifi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Wires up the {@link RestClient} used to call {@code sector-service}
 * synchronously (see {@code com.jp5k.projectnifi.service.SectorClient}).
 *
 * <p>Spring's built-in {@code RestClient} (rather than {@code RestTemplate},
 * which is in maintenance mode, or a generated OpenAPI client) is the choice
 * called out in {@code docs/plan.md}'s architecture for demonstrating direct
 * REST-to-REST service communication — deliberately without service
 * discovery, a config server, or an API gateway (see the roadmap's
 * Stretch/later section); the base URL is just a fixed property instead.
 *
 * <p>No connect/read timeouts are configured yet — the default
 * {@code RestClient} blocks indefinitely on a hung {@code sector-service},
 * which is acceptable for this learning project's current scope but would
 * need addressing before this pattern went anywhere near production.
 */
@Configuration
public class RestClientConfig {

    /**
     * @param sectorServiceBaseUrl base URL of {@code sector-service}, e.g.
     *     {@code http://localhost:8082} (see {@code sector-service.base-url}
     *     in {@code application.properties})
     * @return a {@link RestClient} pre-configured with that base URL, so
     *     callers only need to supply the path (e.g. {@code /sectors/{name}})
     */
    @Bean
    RestClient sectorServiceRestClient(@Value("${sector-service.base-url}") String sectorServiceBaseUrl) {
        return RestClient.builder().baseUrl(sectorServiceBaseUrl).build();
    }
}
