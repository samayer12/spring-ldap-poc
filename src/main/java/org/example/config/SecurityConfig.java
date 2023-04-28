package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.PersonContextMapper;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Set;

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
          .antMatchers("/secure").authenticated()
          .anyRequest().authenticated())
          .formLogin().permitAll().and().logout().logoutSuccessUrl("/");

        return http.build();
    }

    @Bean
    protected LdapAuthoritiesPopulator ldapAuthoritiesPopulator(BaseLdapPathContextSource contextSource) {
        String groupSearchBase = "";

           DefaultLdapAuthoritiesPopulator populi = new DefaultLdapAuthoritiesPopulator(contextSource(), "ou=groups") {

            private static final String ADMIN_ROLE = "ROLE_ADMIN";
            @Override
            public Set<GrantedAuthority> getGroupMembershipRoles(String userDn, String username) {
                Set<GrantedAuthority> groupMembershipRoles = super.getGroupMembershipRoles(userDn, username);

                boolean isMemberOfSpecificAdGroup = false;
                for (GrantedAuthority grantedAuthority : groupMembershipRoles) {

                    if (ADMIN_ROLE.equals(grantedAuthority.toString())) {
                        isMemberOfSpecificAdGroup = true;
                        break;
                    }
                }

                if (!isMemberOfSpecificAdGroup) {

                    throw new BadCredentialsException("User must be a member of " + ADMIN_ROLE);
                }
                return groupMembershipRoles;
            }
        };

        return populi;
    }

    @Bean
    protected AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource, LdapAuthoritiesPopulator authorities) {
        LdapBindAuthenticationManagerFactory factory = new LdapBindAuthenticationManagerFactory(contextSource);
        factory.setUserSearchFilter("(uid={0})");
        factory.setUserSearchBase("ou=people");
        factory.setUserDetailsContextMapper(new PersonContextMapper());
        factory.setLdapAuthoritiesPopulator(ldapAuthoritiesPopulator(contextSource));
        return factory.createAuthenticationManager();
    }

    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        String ldapServer = this.ldapUrl;
        if (!ldapServer.endsWith("/")) ldapServer += "/";
        return new DefaultSpringSecurityContextSource(ldapServer + this.baseDn);
    }
}
