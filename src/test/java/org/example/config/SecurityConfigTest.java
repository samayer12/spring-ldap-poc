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
  "spring.ldap.embedded.ldif=classpath:test-server.ldif",
  "spring.ldap.embedded.base-dn=${spring.ldap.base}",
  "spring.ldap.embedded.port=8389",
  "spring.ldap.embedded.url=ldap://localhost:8389/",
  "spring.ldap.embedded.credential.username=uid=admin",
  "spring.ldap.embedded.credential.password=secret",
  "spring.ldap.embedded.validation.enabled=false",
  "spring.ldap.urls=ldap://localhost:8389/",
  "spring.ldap.username=uid=admin",
  "spring.ldap.password=secret"})
public class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void loginWithValidUserThenAuthenticated() throws Exception {
      SecurityMockMvcRequestBuilders.FormLoginRequestBuilder login = SecurityMockMvcRequestBuilders.formLogin()
        .user("user")
        .password("userpassword");

      mockMvc.perform(login)
        .andExpect(SecurityMockMvcResultMatchers.authenticated().withUsername("user"));
    }

    @Test
    public void loginWithInvalidUserThenUnauthenticated() throws Exception {
      SecurityMockMvcRequestBuilders.FormLoginRequestBuilder login = SecurityMockMvcRequestBuilders.formLogin()
        .user("invalid")
        .password("invalidpassword");

      mockMvc.perform(login)
        .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }
}