package org.example.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
  "spring.ldap.urls=ldap://localhost:1389/",
  "spring.ldap.username=uid=authorized",
  "spring.ldap.password=bitnami1"})
public class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void loginWithValidUserThenAuthenticated() throws Exception {
      SecurityMockMvcRequestBuilders.FormLoginRequestBuilder login = SecurityMockMvcRequestBuilders.formLogin()
        .user("authorized")
        .password("bitnami1");

      mockMvc.perform(login)
        .andExpect(SecurityMockMvcResultMatchers.authenticated().withUsername("authorized"));
    }

    @Test
    public void loginWithInvalidUserThenUnauthenticated() throws Exception {
      SecurityMockMvcRequestBuilders.FormLoginRequestBuilder login = SecurityMockMvcRequestBuilders.formLogin()
        .user("not-a-user")
        .password("password");

      mockMvc.perform(login)
        .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }
}