package com.jp5k.projectnifi.service;

import static org.mockito.Mockito.verify;

import com.jp5k.projectnifi.config.RabbitConfig;
import com.jp5k.projectnifi.dto.StockPriceUpdate;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Unit tests for {@link StockPriceUpdatePublisher}, with {@link RabbitTemplate}
 * mocked — no broker needed.
 */
@ExtendWith(MockitoExtension.class)
class StockPriceUpdatePublisherTests {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Test
    void publishSendsTheUpdateToTheConfiguredExchangeAndRoutingKey() {
        StockPriceUpdatePublisher publisher = new StockPriceUpdatePublisher(rabbitTemplate);
        StockPriceUpdate update =
                new StockPriceUpdate("NVTD", Instant.parse("2026-08-11T09:30:00Z"), new BigDecimal("142.50"), 1200);

        publisher.publish(update);

        verify(rabbitTemplate)
                .convertAndSend(RabbitConfig.STOCK_PRICE_EXCHANGE, RabbitConfig.STOCK_PRICE_ROUTING_KEY, update);
    }
}
