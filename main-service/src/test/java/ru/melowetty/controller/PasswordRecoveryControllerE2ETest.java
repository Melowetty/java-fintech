package ru.melowetty.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.melowetty.service.PasswordRecoveryService;
import ru.melowetty.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(
        properties = {
                "spring.datasource.url=jdbc:tc:postgresql:16-alpine:///db"
        }
)
@Testcontainers
@SpringBootTest
@ActiveProfiles("test-with-db")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PasswordRecoveryControllerE2ETest {
    private static boolean userGenerated = false;

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private UserService userService;

    @SpyBean
    private PasswordRecoveryService passwordRecoveryService;

    private static String token;

    @BeforeEach
    public void createUser() {
        if (!userGenerated) {
            userService.createUser("melowetty", "test");
            userGenerated = true;
        }
    }

    @Test
    @Order(1)
    public void initPasswordRecovery() throws Exception {
        var request = "{\"username\":\"melowetty\"}";

        Mockito.doReturn("9999")
                .when(passwordRecoveryService).generateAuthCode();

        var response = mockMvc.perform(post("/auth/password-recovery")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        token = JsonPath.read(response.getContentAsString(), "$.token");

        Assertions.assertFalse(token.isEmpty());
    }

    @Test
    @Order(2)
    public void enterNotValidAuthCode() throws Exception {
        var request = String.format("{\"token\":\"%s\",\"authCode\":\"1234\"}", token);

        mockMvc.perform(post("/auth/password-recovery/auth-code")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    public void unSuccessAttemptToChangePassword() throws Exception {
        var request = String.format("{\"token\":\"%s\",\"newPassword\":\"test123!T\"}", token);

        var currentPasswordHash = userService.getUserByUsername("melowetty").getPassword();

        mockMvc.perform(post("/auth/password-recovery/new-password")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        var newPasswordHash = userService.getUserByUsername("melowetty").getPassword();

        Assertions.assertEquals(newPasswordHash, currentPasswordHash);
    }

    @Test
    @Order(3)
    public void enterValidAuthCode() throws Exception {
        var request = String.format("{\"token\":\"%s\",\"authCode\":\"9999\"}", token);

        mockMvc.perform(post("/auth/password-recovery/auth-code")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    public void checkPasswordIsChanged() throws Exception {
        var request = String.format("{\"token\":\"%s\",\"newPassword\":\"test123!T\"}", token);

        var currentPasswordHash = userService.getUserByUsername("melowetty").getPassword();

        mockMvc.perform(post("/auth/password-recovery/new-password")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var newPasswordHash = userService.getUserByUsername("melowetty").getPassword();

        Assertions.assertNotEquals(newPasswordHash, currentPasswordHash);

        mockMvc.perform(post("/auth/password-recovery/new-password")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
