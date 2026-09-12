package com.jp5k.projectnifi.sectorservice.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.jp5k.projectnifi.sectorservice.model.Sector;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link SectorMapper}, covering both entity-building overloads
 * and the entity-to-response projection.
 */
class SectorMapperTests {

    private final SectorRequest request = new SectorRequest("Technology", "Tech companies");

    @Test
    void toEntityFromRequestCopiesEveryFieldIncludingName() {
        Sector entity = SectorMapper.toEntity(request);

        assertThat(entity.getName()).isEqualTo("Technology");
        assertThat(entity.getDescription()).isEqualTo("Tech companies");
    }

    @Test
    void toEntityWithNameTakesTheNameFromTheArgumentNotTheBody() {
        SectorRequest bodyWithDifferentName = new SectorRequest("Wrong", "Tech companies");

        Sector entity = SectorMapper.toEntity("Technology", bodyWithDifferentName);

        assertThat(entity.getName()).isEqualTo("Technology");
        assertThat(entity.getDescription()).isEqualTo("Tech companies");
    }

    @Test
    void toResponseProjectsEveryEntityField() {
        Sector entity = new Sector("Technology", "Tech companies");

        SectorResponse response = SectorMapper.toResponse(entity);

        assertThat(response).isEqualTo(new SectorResponse("Technology", "Tech companies"));
    }
}
