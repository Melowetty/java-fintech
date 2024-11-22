package ru.melowetty.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.melowetty.repository.UserRepository;
import ru.melowetty.service.JwtService;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(
        properties = {
                "spring.datasource.url=jdbc:tc:postgresql:16-alpine:///auth-controller-tests"
        }
)
@Testcontainers
@SpringBootTest
@ActiveProfiles("test-with-db")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerE2ETest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private static String userToken;

    @Test
    @Order(1)
    void testSuccessRegisterUser() throws Exception {
        var request = "{\"username\":\"melowetty\",\"password\":\"test123!\"}";

        var response = mockMvc.perform(post("/auth/register")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        userToken = JsonPath.read(response.getContentAsString(), "$.token");

        Assertions.assertFalse(userToken.isEmpty());
        Assertions.assertTrue(userRepository.findUserByUsername("melowetty").isPresent());
        Assertions.assertTrue(jwtService.isTokenValid(userToken));
    }

    @Test
    @Order(2)
    void testUnSuccessAccessWithNoToken() throws Exception {
        mockMvc.perform(get("/api/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(2)
    void testSuccessAccessWithTokenFromRegister() throws Exception {
        mockMvc.perform(get("/api/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(2)
    void testUnSuccessLoginWhenNotValidPassword() throws Exception {
        var request = "{\"username\":\"melowetty\",\"password\":\"test1234!\", \"rememberMe\":\"false\"}";

        mockMvc.perform(post("/auth/login")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    void testSuccessLoginWithoutRememberMeWhenValidPassword() throws Exception {
        var request = "{\"username\":\"melowetty\",\"password\":\"test123!\", \"rememberMe\":false}";

        var response = mockMvc.perform(post("/auth/login")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        userToken = JsonPath.read(response.getContentAsString(), "$.token");

        Assertions.assertFalse(userToken.isEmpty());
        Assertions.assertTrue(jwtService.isTokenValid(userToken));

        var tokenExpirationDate = jwtService.extractExpirationDate(userToken);
        var currentDate = LocalDateTime.now();

        long tokenLifeTime = TimeUnit.MINUTES.toChronoUnit().between(currentDate, tokenExpirationDate);
        Assertions.assertTrue(tokenLifeTime >= 5 && tokenLifeTime <= 10);
    }

    @Test
    @Order(3)
    void testSuccessLoginWithRememberMeWhenValidPassword() throws Exception {
        var request = "{\"username\":\"melowetty\",\"password\":\"test123!\", \"rememberMe\":true}";

        var response = mockMvc.perform(post("/auth/login")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        String token = JsonPath.read(response.getContentAsString(), "$.token");

        Assertions.assertFalse(token.isEmpty());
        Assertions.assertTrue(jwtService.isTokenValid(token));

        var tokenExpirationDate = jwtService.extractExpirationDate(token);
        var currentDate = LocalDateTime.now();

        long tokenLifeTime = TimeUnit.DAYS.toChronoUnit().between(currentDate, tokenExpirationDate);
        Assertions.assertTrue(tokenLifeTime >= 29 && tokenLifeTime <= 30);
    }

    @Test
    @Order(4)
    void testSuccessAccessWithTokenFromLogin() throws Exception {
        mockMvc.perform(get("/api/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    void testUnSuccessAccessToAdminEndpointsWithTokenFromLogin() throws Exception {
        mockMvc.perform(post("/api/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(5)
    void testSuccessLogout() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUnSuccessLoginWithNoSignedToken() throws Exception {
        var notSignedToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6Im1lbG93ZXR0eSIsInJvbGVzIjpbIlJPTEVfVVNFUiIsIlJPTEVfQURNSU4iXSwic3ViIjoibWVsb3dldHR5IiwiaWF0IjoxNzMyMjUxOTE4LCJleHAiOjE3MzQ4NDM5MTh9.XUX7h-k5WDtUtkYz-SNNDtDDXKaoX-9yLJzycgnA3u0";
        mockMvc.perform(post("/api/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + notSignedToken))
                .andExpect(status().isUnauthorized());
    }
}
