package com.jp5k.projectnifi.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

/**
 * Unit test for {@link RestClientConfig}'s bean method.
 */
class RestClientConfigTests {

    @Test
    void sectorServiceRestClientBuildsANonNullClient() {
        RestClient restClient = new RestClientConfig().sectorServiceRestClient("http://localhost:8082");

        assertThat(restClient).isNotNull();
    }
}
