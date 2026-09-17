package com.jp5k.projectnifi.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jp5k.projectnifi.dto.StockPriceUpdate;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * Unit tests for {@link RabbitConfig}'s bean methods.
 */
class RabbitConfigTests {

    private final RabbitConfig config = new RabbitConfig();

    @Test
    void stockPriceExchangeHasExpectedName() {
        DirectExchange exchange = config.stockPriceExchange();

        assertThat(exchange.getName()).isEqualTo(RabbitConfig.STOCK_PRICE_EXCHANGE);
    }

    @Test
    void stockPriceQueueHasExpectedName() {
        Queue queue = config.stockPriceQueue();

        assertThat(queue.getName()).isEqualTo(RabbitConfig.STOCK_PRICE_QUEUE);
    }

    @Test
    void stockPriceBindingConnectsTheQueueAndExchangeWithTheExpectedRoutingKey() {
        Queue queue = config.stockPriceQueue();
        DirectExchange exchange = config.stockPriceExchange();

        Binding binding = config.stockPriceBinding(queue, exchange);

        assertThat(binding.getExchange()).isEqualTo(RabbitConfig.STOCK_PRICE_EXCHANGE);
        assertThat(binding.getDestination()).isEqualTo(RabbitConfig.STOCK_PRICE_QUEUE);
        assertThat(binding.getRoutingKey()).isEqualTo(RabbitConfig.STOCK_PRICE_ROUTING_KEY);
    }

    @Test
    void jsonMessageConverterSerializesInstantFieldsUsingTheGivenObjectMapper() {
        // Mirrors Spring Boot's auto-configured ObjectMapper bean: JSR-310
        // module registered, and WRITE_DATES_AS_TIMESTAMPS disabled (Boot's
        // default) so an Instant serializes as an ISO-8601 string rather than
        // a numeric epoch timestamp.
        ObjectMapper objectMapper = new ObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MessageConverter converter = config.jsonMessageConverter(objectMapper);
        StockPriceUpdate update =
                new StockPriceUpdate("NVTD", Instant.parse("2026-08-11T09:30:00Z"), new BigDecimal("142.50"), 1200);

        Message message = converter.toMessage(update, new MessageProperties());

        assertThat(new String(message.getBody(), StandardCharsets.UTF_8))
                .contains("\"symbol\":\"NVTD\"", "2026-08-11T09:30:00Z");
    }
}
