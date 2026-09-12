package com.jp5k.projectnifi.sectorservice.repository;

import com.jp5k.projectnifi.sectorservice.model.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Sector}.
 *
 * <p>No custom query methods yet — {@link JpaRepository} already provides
 * everything the basic CRUD endpoints need (save, findById, findAll,
 * existsById, delete). Add derived/custom queries here only once a real need
 * for them shows up.
 */
public interface SectorRepository extends JpaRepository<Sector, String> {
}
