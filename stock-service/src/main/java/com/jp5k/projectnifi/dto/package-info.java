/**
 * Request/response DTOs for {@code stock-service}'s REST API, plus the
 * event payload types it publishes onto RabbitMQ (e.g. {@code StockPriceUpdate}).
 *
 * <p>Classes here define the shapes actually sent and received over HTTP or
 * onto the message broker, kept separate from the JPA entities in the
 * {@code model} package so the persistence model can evolve independently of
 * either external contract.
 */
package com.jp5k.projectnifi.dto;
