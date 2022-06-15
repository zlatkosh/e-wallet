package com.zlatkosh.ewallet.service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, EWalletAuthenticationFilter eWalletAuthenticationFilter, EWalletAuthorizationFilter eWalletAuthorizationFilter) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests((authz) -> authz
                        .antMatchers("/login", "/access/refresh_token").permitAll()
                        .antMatchers(HttpMethod.GET).hasAuthority("ADMIN")
                        .antMatchers(HttpMethod.PUT).hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilter(eWalletAuthenticationFilter)
                .addFilter(new DefaultLoginPageGeneratingFilter())
                .addFilterBefore(eWalletAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.setUsersByUsernameQuery("SELECT username, password, true FROM user_data WHERE username = ?");
        users.setAuthoritiesByUsernameQuery("SELECT username, role_name FROM role WHERE username = ?");
        return users;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
