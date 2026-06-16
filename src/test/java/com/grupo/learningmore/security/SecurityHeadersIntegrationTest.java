package com.grupo.learningmore.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class SecurityHeadersIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testCrossOriginResourcePolicyHeaderIsPresent() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cross-Origin-Resource-Policy", "same-origin"));
    }

    @Test
    public void testFetchMetadataFilterAllowsSameOrigin() throws Exception {
        mockMvc.perform(get("/api/health")
                        .header("Sec-Fetch-Site", "same-origin"))
                .andExpect(status().isOk());
    }

    @Test
    public void testFetchMetadataFilterBlocksForbiddenCrossSite() throws Exception {
        mockMvc.perform(get("/api/health")
                        .header("Sec-Fetch-Site", "cross-site")
                        .header("Sec-Fetch-Mode", "no-cors")
                        .header("Sec-Fetch-Dest", "image"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testFetchMetadataFilterAllowsCrossSiteNavigation() throws Exception {
        mockMvc.perform(get("/api/health")
                        .header("Sec-Fetch-Site", "cross-site")
                        .header("Sec-Fetch-Mode", "navigate")
                        .header("Sec-Fetch-Dest", "document"))
                .andExpect(status().isOk());
    }

    @Test
    public void testFetchMetadataFilterAllowsNone() throws Exception {
        mockMvc.perform(get("/api/health")
                        .header("Sec-Fetch-Site", "none"))
                .andExpect(status().isOk());
    }
}
