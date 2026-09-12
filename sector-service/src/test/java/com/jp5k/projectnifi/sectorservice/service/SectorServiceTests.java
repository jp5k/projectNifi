package com.jp5k.projectnifi.sectorservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jp5k.projectnifi.sectorservice.model.Sector;
import com.jp5k.projectnifi.sectorservice.repository.SectorRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link SectorService}, with {@link SectorRepository} mocked.
 */
@ExtendWith(MockitoExtension.class)
class SectorServiceTests {

    @Mock
    private SectorRepository sectorRepository;

    private SectorService sectorService;

    private final Sector sector = new Sector("Technology", "Tech companies");

    @BeforeEach
    void setUp() {
        sectorService = new SectorService(sectorRepository);
    }

    @Test
    void findAllReturnsEverySectorFromTheRepository() {
        when(sectorRepository.findAll()).thenReturn(List.of(sector));

        assertThat(sectorService.findAll()).containsExactly(sector);
    }

    @Test
    void findByNameReturnsSectorWhenPresent() {
        when(sectorRepository.findById("Technology")).thenReturn(Optional.of(sector));

        assertThat(sectorService.findByName("Technology")).contains(sector);
    }

    @Test
    void findByNameReturnsEmptyWhenMissing() {
        when(sectorRepository.findById("Unknown")).thenReturn(Optional.empty());

        assertThat(sectorService.findByName("Unknown")).isEmpty();
    }

    @Test
    void saveDelegatesToRepository() {
        when(sectorRepository.save(sector)).thenReturn(sector);

        assertThat(sectorService.save(sector)).isEqualTo(sector);
    }

    @Test
    void deleteByNameDeletesAndReturnsTrueWhenPresent() {
        when(sectorRepository.existsById("Technology")).thenReturn(true);

        assertThat(sectorService.deleteByName("Technology")).isTrue();
        verify(sectorRepository).deleteById("Technology");
    }

    @Test
    void deleteByNameReturnsFalseWhenMissing() {
        when(sectorRepository.existsById("Unknown")).thenReturn(false);

        assertThat(sectorService.deleteByName("Unknown")).isFalse();
    }
}
