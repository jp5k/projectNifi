package com.jp5k.projectnifi.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Calls {@code sector-service} synchronously (Spring's built-in
 * {@link RestClient}) to check whether a named sector exists.
 *
 * <p>Lives in the {@code service} package rather than a dedicated
 * {@code client} package — this package's own {@code package-info.java}
 * already calls out "coordinating with other services (e.g. calling
 * sector-service)" as a service-layer responsibility, so a separate package
 * would just split one concern across two places.
 */
@Component
public class SectorClient {

    private final RestClient sectorServiceRestClient;

    public SectorClient(RestClient sectorServiceRestClient) {
        this.sectorServiceRestClient = sectorServiceRestClient;
    }

    /**
     * Checks whether {@code sector-service} has a sector with the given name,
     * via {@code GET /sectors/{name}}.
     *
     * @param sectorName the sector name to check, e.g. {@code "Technology"}
     * @return {@code true} if the sector exists, {@code false} if
     *     {@code sector-service} responded {@code 404 Not Found}
     * @throws RestClientException if {@code sector-service} is unreachable or
     *     returns any other error response — deliberately left to propagate
     *     rather than caught here; {@code GlobalExceptionHandler}'s catch-all
     *     turns it into a generic {@code 500} without leaking detail, which is
     *     the right outcome for "the dependency it needs is broken", as
     *     opposed to "the client asked for a sector that doesn't exist".
     */
    public boolean exists(String sectorName) {
        try {
            sectorServiceRestClient
                    .get()
                    .uri("/sectors/{name}", sectorName)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }
}
