package com.jp5k.projectnifi.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declares the RabbitMQ topology {@code stock-service} publishes
 * {@code StockPriceUpdate} events to (publishing itself is a later roadmap
 * item — this step is just the wiring/topology).
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
 * connection to the broker is actually created, not eagerly at app startup.
 * Nothing in {@code stock-service} opens a connection yet (that happens the
 * moment something publishes, in the next roadmap item), so don't expect
 * {@code stock.price}/{@code stock.price.updates} to show up in the RabbitMQ
 * management UI just from starting the app with no publish having happened —
 * confirmed by explicitly forcing a connection open (a temporary diagnostic,
 * since removed) and watching the declaration succeed immediately once that
 * connection existed.
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
}
