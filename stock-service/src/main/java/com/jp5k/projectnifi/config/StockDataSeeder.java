package com.jp5k.projectnifi.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp5k.projectnifi.model.Stock;
import com.jp5k.projectnifi.repository.StockRepository;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * Seeds {@code stock-service}'s database with the fictional companies from
 * {@code sample-data/stocks.json} on startup, so there's always something to
 * query on a fresh boot without manual setup.
 *
 * <p>{@code stocks.json} is pulled onto the classpath at build time (see
 * {@code stock-service/pom.xml}) from its single source of truth in
 * {@code sample-data/}, rather than duplicating it under
 * {@code src/main/resources}.
 */
@Configuration
public class StockDataSeeder {

    private static final Logger log = LoggerFactory.getLogger(StockDataSeeder.class);

    /**
     * Loads {@code sample-data/stocks.json} and saves each entry, but only if
     * the {@code stocks} table is currently empty — so re-running the app
     * against a persistent database doesn't re-seed or duplicate rows.
     */
    @Bean
    CommandLineRunner seedStocks(StockRepository stockRepository, ObjectMapper objectMapper) {
        return args -> {
            if (stockRepository.count() > 0) {
                log.info("Stocks table already has data, skipping seed");
                return;
            }

            ClassPathResource resource = new ClassPathResource("sample-data/stocks.json");
            List<StockSeed> seeds = objectMapper.readValue(
                    resource.getInputStream(), new TypeReference<List<StockSeed>>() {
                    });

            List<Stock> stocks = seeds.stream()
                    .map(seed -> new Stock(seed.symbol(), seed.name(), seed.sector(), seed.basePrice()))
                    .toList();
            stockRepository.saveAll(stocks);

            log.info("Seeded {} stocks from sample-data/stocks.json", stocks.size());
        };
    }

    /**
     * Shape of an entry in {@code stocks.json}. Kept separate from {@link Stock}
     * rather than deserializing straight into the entity, so JSON mapping
     * concerns don't leak into the JPA model. {@code classification} is present
     * in the JSON but deliberately ignored — {@link Stock} doesn't model it
     * yet, that's the Data Classification & Access Control milestone.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record StockSeed(String symbol, String name, String sector, BigDecimal basePrice) {
    }
}
