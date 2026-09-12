package com.jp5k.projectnifi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

/**
 * Unit tests for {@link SectorClient}, with the HTTP call to
 * {@code sector-service} intercepted by {@link MockRestServiceServer} —
 * no real network call, no need for {@code sector-service} to be running.
 */
class SectorClientTests {

    private static final String BASE_URL = "http://sector-service.test";

    private MockRestServiceServer mockServer;
    private SectorClient sectorClient;

    private void bindClient() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        mockServer = MockRestServiceServer.bindTo(builder).build();
        sectorClient = new SectorClient(builder.build());
    }

    @Test
    void existsReturnsTrueWhenSectorServiceRespondsOk() {
        bindClient();
        mockServer.expect(requestTo(BASE_URL + "/sectors/Technology")).andRespond(withSuccess());

        assertThat(sectorClient.exists("Technology")).isTrue();
        mockServer.verify();
    }

    @Test
    void existsReturnsFalseWhenSectorServiceRespondsNotFound() {
        bindClient();
        mockServer.expect(requestTo(BASE_URL + "/sectors/Unknown")).andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThat(sectorClient.exists("Unknown")).isFalse();
        mockServer.verify();
    }
}
