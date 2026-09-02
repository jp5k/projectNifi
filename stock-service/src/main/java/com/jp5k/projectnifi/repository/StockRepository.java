package com.jp5k.projectnifi.repository;

import com.jp5k.projectnifi.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Stock}.
 *
 * <p>No custom query methods yet — {@link JpaRepository} already provides
 * everything the upcoming basic CRUD endpoints need (save, findById,
 * findAll, delete). Add derived/custom queries here only once a real need
 * for them shows up.
 */
public interface StockRepository extends JpaRepository<Stock, String> {
}
