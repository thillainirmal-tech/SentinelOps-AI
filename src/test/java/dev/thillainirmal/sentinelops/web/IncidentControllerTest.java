package dev.thillainirmal.sentinelops.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IncidentControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void analyzesAndApprovesAProtectedAction() throws Exception {
        String body = mockMvc.perform(post("/api/incidents/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Checkout outage",
                                  "description": "All users see HTTP 503 because the service is unavailable.",
                                  "service": "checkout-service"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.severity").value("CRITICAL"))
                .andExpect(jsonPath("$.agentSteps.length()").value(4))
                .andReturn().getResponse().getContentAsString();

        JsonNode analysis = objectMapper.readTree(body);
        JsonNode protectedAction = analysis.get("actions").findValues("risk").stream()
                .filter(node -> "DANGEROUS".equals(node.asText()))
                .findFirst()
                .orElse(null);
        assertThat(protectedAction).isNotNull();

        JsonNode action = null;
        for (JsonNode candidate : analysis.get("actions")) {
            if ("DANGEROUS".equals(candidate.get("risk").asText())) {
                action = candidate;
                break;
            }
        }
        assertThat(action).isNotNull();

        mockMvc.perform(post("/api/incidents/{incidentId}/actions/{actionId}/approve",
                        analysis.get("incidentId").asText(), action.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actions[?(@.id == '" + action.get("id").asText() + "')].status")
                        .value("APPROVED"));
    }

    @Test
    void rejectsBlankIncident() throws Exception {
        mockMvc.perform(post("/api/incidents/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"description\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"));
    }
}
