package com.jp5k.projectnifi.sectorservice.service;

import com.jp5k.projectnifi.sectorservice.model.Sector;
import com.jp5k.projectnifi.sectorservice.repository.SectorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Business logic for {@link Sector}.
 *
 * <p>Currently a thin pass-through to {@link SectorRepository} — basic CRUD
 * only, mirroring {@code stock-service}'s {@code StockService}. Works in
 * terms of the {@link Sector} entity, not the REST DTOs: the controller owns
 * the HTTP contract and maps to/from DTOs.
 */
@Service
public class SectorService {

    private final SectorRepository sectorRepository;

    public SectorService(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }

    /** Returns every {@link Sector}. No pagination yet — small, fixed reference data. */
    public List<Sector> findAll() {
        return sectorRepository.findAll();
    }

    /** Returns the {@link Sector} with the given name, if it exists. */
    public Optional<Sector> findByName(String name) {
        return sectorRepository.findById(name);
    }

    /** Creates or fully replaces the {@link Sector} with the given name. */
    public Sector save(Sector sector) {
        return sectorRepository.save(sector);
    }

    /**
     * Deletes the {@link Sector} with the given name.
     *
     * @return {@code true} if a sector with that name existed and was
     *     deleted, {@code false} if there was nothing to delete
     */
    public boolean deleteByName(String name) {
        if (!sectorRepository.existsById(name)) {
            return false;
        }
        sectorRepository.deleteById(name);
        return true;
    }
}
