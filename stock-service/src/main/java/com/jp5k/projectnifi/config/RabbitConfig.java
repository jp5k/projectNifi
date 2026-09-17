package com.jp5k.projectnifi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declares the RabbitMQ topology {@code stock-service} publishes
 * {@code StockPriceUpdate} events to, and the JSON message converter used to
 * serialize them (see {@code service/StockPriceUpdatePublisher}).
 *
 * <p>A plain {@link DirectExchange} + single queue is deliberately used to
 * start, per {@code docs/plan.md}'s Messaging & Dataflow milestone ("simple
 * queue/exchange to start"). The Data Classification & Access Control
 * milestone later swaps this for a {@code TopicExchange} with routing key
 * {@code stock.price.<classification>}, so consumers (notification-service,
 * the NiFi flow) can each bind only to the classifications they're allowed to
 * see — not needed yet since {@code Stock} has no {@code classification}
 * field until that milestone.
 *
 * <p>Spring Boot's {@code AmqpAdmin} (auto-configured by
 * {@code spring-boot-starter-amqp}) declares every {@link Queue}/
 * {@link DirectExchange}/{@link Binding} bean found here — but only once a
 * connection to the broker is actually created, not eagerly at app startup
 * (confirmed while diagnosing why the exchange/queue didn't appear before
 * anything published — connecting alone doesn't trigger it, only a real
 * connection does). Now that {@code StockPriceUpdatePublisher} exists,
 * {@code POST /stocks/{symbol}/price-updates} is what opens that first
 * connection and makes {@code stock.price}/{@code stock.price.updates}
 * appear in the RabbitMQ management UI — starting {@code stock-service} on
 * its own still won't, since nothing publishes on startup.
 */
@Configuration
public class RabbitConfig {

    /** Name of the exchange {@code StockPriceUpdate} events publish to. */
    public static final String STOCK_PRICE_EXCHANGE = "stock.price";

    /** Name of the queue bound to {@link #STOCK_PRICE_EXCHANGE}. */
    public static final String STOCK_PRICE_QUEUE = "stock.price.updates";

    /**
     * Fixed routing key used for the single binding below, until the Data
     * Classification milestone replaces it with a per-classification key
     * ({@code stock.price.<classification>}) on a {@code TopicExchange}.
     */
    public static final String STOCK_PRICE_ROUTING_KEY = "stock.price.update";

    @Bean
    DirectExchange stockPriceExchange() {
        return new DirectExchange(STOCK_PRICE_EXCHANGE);
    }

    @Bean
    Queue stockPriceQueue() {
        return new Queue(STOCK_PRICE_QUEUE);
    }

    @Bean
    Binding stockPriceBinding(Queue stockPriceQueue, DirectExchange stockPriceExchange) {
        return BindingBuilder.bind(stockPriceQueue).to(stockPriceExchange).with(STOCK_PRICE_ROUTING_KEY);
    }

    /**
     * Serializes published events (e.g. {@code StockPriceUpdate}) as JSON
     * rather than Java serialization (the {@code RabbitTemplate} default).
     * Built from the Boot-managed {@link ObjectMapper} bean — the same one
     * the REST API's own JSON (de)serialization uses — rather than
     * {@code Jackson2JsonMessageConverter}'s own bare default, so
     * {@code Instant}/{@code BigDecimal} fields on {@code StockPriceUpdate}
     * serialize the same way here as they already do over HTTP (the default
     * {@code ObjectMapper} has no JSR-310 module registered on its own).
     *
     * <p>Spring Boot's {@code RabbitTemplateConfigurer} auto-wires whichever
     * single {@link MessageConverter} bean it finds into the autoconfigured
     * {@code RabbitTemplate} — no further wiring needed.
     */
    @Bean
    MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
