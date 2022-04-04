package com.softbinator_labs.project.good_deeds.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import java.security.SecureRandom;

@KeycloakConfiguration
@Import(KeycloakSpringBootConfigResolver.class)
class KeycloakConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10, new SecureRandom());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http
            .cors().and().csrf().disable()
            .authorizeRequests()
            .antMatchers("/login", "/refresh").permitAll()
            .antMatchers("/users/register", "/users/processRegister", "/users/processRegisterAdmin").permitAll()
            .antMatchers("/users/verify").permitAll()
            .antMatchers("/users/info", "/users/myVouchers", "/users/purchaseVoucher/**").authenticated()
            .antMatchers("/users/registerAdmin").hasRole("ADMIN")
            .antMatchers("/creditCards/add").hasRole("ADMIN")
            .antMatchers("/vouchers/add", "/vouchers/edit/**", "/vouchers/delete/**").hasRole("ADMIN")
            .antMatchers("/vouchers").authenticated()
            .antMatchers("/organisations/viewAll", "/organisations/view/**").hasRole("ADMIN")
            .antMatchers("/organisations/myOrganisation", "/organisations/add", "/organisations/edit", "/organisations/delete").authenticated()
            .antMatchers("/organisations/delete/**").hasRole("ADMIN")
            .antMatchers("/charityEvents/viewAll", "charityEvents/view/**").authenticated()
            .antMatchers("/charityEvents/add", "/charityEvents/edit/**", "/charityEvents/delete/{id}").hasRole("ORGANISER")
            .antMatchers("/donations/makeDonation/**").authenticated()
            .antMatchers("/donations/view/**").hasRole("ADMIN")
            .anyRequest().authenticated();
    }
}
