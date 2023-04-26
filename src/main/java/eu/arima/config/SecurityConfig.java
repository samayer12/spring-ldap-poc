package eu.arima.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import java.util.Set;

/**
 * Security Configuration - LDAP and HTTP Authorizations.
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.ldap.urls:ldap://localhost:1389}")
    private String ldapUrl;

    @Value("${spring.ldap.base:dc=example,dc=org}")
    private String baseDn;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication()
                .contextSource(contextSource())
                .userSearchFilter("(uid={0})")
                .userSearchBase("ou=users")
                .ldapAuthoritiesPopulator(ldapAuthoritiesPopulator())
                .groupSearchFilter("(member={0})")
                .groupSearchBase("ou=groups")
                .passwordCompare()
                    .passwordEncoder(new LdapShaPasswordEncoder())
                    .passwordAttribute("userPassword")
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .anyRequest().authenticated().and()
                .formLogin().permitAll().and().logout().logoutSuccessUrl("/");
    }

    @Bean
    public LdapAuthoritiesPopulator ldapAuthoritiesPopulator() {
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
    public DefaultSpringSecurityContextSource contextSource() {
        String ldapServer = this.ldapUrl;
        if (!ldapServer.endsWith("/")) ldapServer += "/";
        return new DefaultSpringSecurityContextSource(ldapServer + this.baseDn);
    }
}
