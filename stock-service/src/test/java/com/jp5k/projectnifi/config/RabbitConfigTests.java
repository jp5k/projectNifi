package com.jp5k.projectnifi.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

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
}
