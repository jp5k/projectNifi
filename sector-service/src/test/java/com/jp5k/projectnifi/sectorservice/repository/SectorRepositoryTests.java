package com.jp5k.projectnifi.sectorservice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.jp5k.projectnifi.sectorservice.model.Sector;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * Verifies {@link SectorRepository} persists and retrieves {@link Sector}
 * entities correctly against the H2 test database.
 */
@DataJpaTest
class SectorRepositoryTests {

    @Autowired
    private SectorRepository sectorRepository;

    @Test
    void savesAndFindsSectorByName() {
        Sector sector = new Sector("Technology", "Tech companies");

        sectorRepository.save(sector);

        Optional<Sector> found = sectorRepository.findById("Technology");
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Tech companies");
    }
}
