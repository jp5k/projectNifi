/**
 * Business/service layer for {@code stock-service}.
 *
 * <p>Classes here contain the application's business logic — orchestrating
 * calls to the {@code repository} package, enforcing rules that don't belong
 * on the entity itself, and coordinating with other services (e.g. calling
 * {@code sector-service}, publishing to RabbitMQ). Controllers call into this
 * package rather than talking to repositories directly.
 */
package com.jp5k.projectnifi.service;
