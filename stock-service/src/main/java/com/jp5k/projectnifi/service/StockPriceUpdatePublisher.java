package com.jp5k.projectnifi.service;

import com.jp5k.projectnifi.config.RabbitConfig;
import com.jp5k.projectnifi.dto.StockPriceUpdate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Publishes {@link StockPriceUpdate} events onto RabbitMQ, using the
 * exchange/routing key declared in {@link RabbitConfig}.
 *
 * <p>A dedicated collaborator, mirroring {@link SectorClient}'s shape — a
 * thin wrapper around one outbound integration, kept separate from
 * {@link StockService} so that class stays about {@code Stock} persistence
 * and delegates external calls (sector validation, now publishing) to
 * single-purpose classes.
 */
@Service
public class StockPriceUpdatePublisher {

    private final RabbitTemplate rabbitTemplate;

    public StockPriceUpdatePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publishes {@code update} to {@link RabbitConfig#STOCK_PRICE_EXCHANGE}
     * with routing key {@link RabbitConfig#STOCK_PRICE_ROUTING_KEY}. Fire-and-forget:
     * the broker connection is created here if one doesn't already exist (see
     * {@code RabbitConfig}'s Javadoc on why declarations don't happen any
     * earlier than this).
     */
    public void publish(StockPriceUpdate update) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.STOCK_PRICE_EXCHANGE, RabbitConfig.STOCK_PRICE_ROUTING_KEY, update);
    }
}
