package ru.melowetty.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityEndpointsTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void attemptAccessToSecuredEndpoint() throws Exception {
        mockMvc.perform(get("/api/"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser()
    @Test
    void successAttemptAccessToSecuredEndpoint() throws Exception {
        mockMvc.perform(get("/api/"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser()
    @Test
    void unSuccessAttemptAccessToSecuredEndpointForAdmins() throws Exception {
        mockMvc.perform(post("/api/"))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void successAttemptAccessToSecuredEndpointForAdmins() throws Exception {
        mockMvc.perform(post("/api/"))
                .andExpect(status().isNotFound());
    }
}
