package com.jp5k.projectnifi.sectorservice.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jp5k.projectnifi.sectorservice.model.Sector;
import com.jp5k.projectnifi.sectorservice.service.SectorService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for {@link SectorController}, exercising the full
 * request/response cycle through {@link MockMvc} with {@link SectorService}
 * mocked out.
 */
@WebMvcTest(SectorController.class)
class SectorControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SectorService sectorService;

    private final Sector sector = new Sector("Technology", "Tech companies");

    @Test
    void findAllReturnsEverySector() throws Exception {
        when(sectorService.findAll()).thenReturn(List.of(sector));

        mockMvc.perform(get("/sectors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Technology"))
                .andExpect(jsonPath("$[0].description").value("Tech companies"));
    }

    @Test
    void findByNameReturnsSectorWhenPresent() throws Exception {
        when(sectorService.findByName("Technology")).thenReturn(Optional.of(sector));

        mockMvc.perform(get("/sectors/Technology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Tech companies"));
    }

    @Test
    void findByNameReturns404ProblemDetailWhenMissing() throws Exception {
        when(sectorService.findByName("Unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/sectors/Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Sector not found"))
                .andExpect(jsonPath("$.detail").value("No sector found with name 'Unknown'"))
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(jsonPath("$.stackTrace").doesNotExist())
                .andExpect(jsonPath("$.exception").doesNotExist());
    }

    @Test
    void createReturns201WithCreatedSector() throws Exception {
        when(sectorService.save(any(Sector.class))).thenReturn(sector);

        mockMvc.perform(post("/sectors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Technology","description":"Tech companies"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Technology"));
    }

    @Test
    void updateReturnsUpdatedSectorWhenPresent() throws Exception {
        Sector updated = new Sector("Technology", "Updated description");
        when(sectorService.findByName("Technology")).thenReturn(Optional.of(sector));
        when(sectorService.save(any(Sector.class))).thenReturn(updated);

        mockMvc.perform(put("/sectors/Technology")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Technology","description":"Updated description"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void updateReturns404WhenMissing() throws Exception {
        when(sectorService.findByName("Unknown")).thenReturn(Optional.empty());

        mockMvc.perform(put("/sectors/Unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Unknown","description":"Doesn't matter"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.detail").value("No sector found with name 'Unknown'"));
    }

    @Test
    void deleteReturns204WhenPresent() throws Exception {
        when(sectorService.deleteByName("Technology")).thenReturn(true);

        mockMvc.perform(delete("/sectors/Technology")).andExpect(status().isNoContent());
        verify(sectorService).deleteByName(eq("Technology"));
    }

    @Test
    void deleteReturns404WhenMissing() throws Exception {
        when(sectorService.deleteByName("Unknown")).thenReturn(false);

        mockMvc.perform(delete("/sectors/Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.detail").value("No sector found with name 'Unknown'"));
    }

    @Test
    void createReturns400ProblemDetailWithPerFieldErrorsWhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/sectors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"  ","description":"Tech companies"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").value("must not be blank"))
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(jsonPath("$.stackTrace").doesNotExist());

        verifyNoInteractions(sectorService);
    }

    @Test
    void createReturns400WhenNameIsMissing() throws Exception {
        mockMvc.perform(post("/sectors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"Tech companies"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(sectorService);
    }

    @Test
    void createSucceedsWithoutADescription() throws Exception {
        Sector noDescription = new Sector("Technology", null);
        when(sectorService.save(any(Sector.class))).thenReturn(noDescription);

        mockMvc.perform(post("/sectors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Technology"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Technology"));
    }

    @Test
    void updateReturns400OnInvalidBodyWithoutCheckingExistence() throws Exception {
        mockMvc.perform(put("/sectors/Technology")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"  ","description":"Tech companies"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(sectorService);
    }

    @Test
    void unexpectedServiceExceptionBecomesGeneric500WithNoLeakedDetail() throws Exception {
        when(sectorService.findAll())
                .thenThrow(new IllegalStateException("H2 connection pool exhausted at com.example.Secret"));

        mockMvc.perform(get("/sectors"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Internal server error"))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred. Please try again later."))
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(jsonPath("$.stackTrace").doesNotExist())
                .andExpect(jsonPath("$.exception").doesNotExist())
                // the underlying message must not appear anywhere in the body
                .andExpect(content().string(not(containsString("H2 connection pool"))));
    }

    @Test
    void malformedJsonBodyBecomes400NotAServerError() throws Exception {
        mockMvc.perform(post("/sectors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ not json "))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(sectorService);
    }
}
