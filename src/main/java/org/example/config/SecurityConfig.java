package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.PersonContextMapper;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration - LDAP and HTTP Authorizations.
 */
@Configuration
public class SecurityConfig {

    @Value("${spring.ldap.urls:ldap://localhost:1389}")
    private String ldapUrl;

    @Value("${spring.ldap.base:dc=example,dc=org}")
    private String baseDn;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests((authz) -> authz
          .antMatchers("/").permitAll()
          .anyRequest().authenticated())
          .formLogin().permitAll().and().logout().logoutSuccessUrl("/");

        return http.build();
    }

    @Bean
    protected LdapAuthoritiesPopulator ldapAuthoritiesPopulator(BaseLdapPathContextSource contextSource) {
        String groupSearchBase = "";
        DefaultLdapAuthoritiesPopulator authorities =
          new DefaultLdapAuthoritiesPopulator(contextSource, groupSearchBase);
        authorities.setGroupSearchFilter("member={0}");

        return authorities;
    }

    @Bean
    protected AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource, LdapAuthoritiesPopulator authorities) {
        LdapBindAuthenticationManagerFactory factory = new LdapBindAuthenticationManagerFactory(contextSource);
        factory.setUserSearchFilter("(uid={0})");
        factory.setUserSearchBase("ou=people");
        factory.setUserDetailsContextMapper(new PersonContextMapper());
        return factory.createAuthenticationManager();
    }

    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        String ldapServer = this.ldapUrl;
        if (!ldapServer.endsWith("/")) ldapServer += "/";
        return new DefaultSpringSecurityContextSource(ldapServer + this.baseDn);
    }
}
