package com.ptc.gateway.security.config;

import com.ptc.gateway.security.AuthenticationManager;
import com.ptc.gateway.security.JsonServerLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authorization.ExceptionTranslationWebFilter;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private ServerAuthenticationConverter authenticationConverter;

    @Autowired
    private ReactiveAuthorizationManager authorizeConfigManager;

    @Autowired
    private ServerAuthenticationEntryPoint serverAuthenticationEntryPoint;

    @Autowired
    private ServerAuthenticationSuccessHandler jsonServerAuthenticationSuccessHandler;

    @Autowired
    private ServerAuthenticationFailureHandler jsonServerAuthenticationFailureHandler;

    @Autowired
    private JsonServerLogoutSuccessHandler jsonServerLogoutSuccessHandler;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ServerAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private ServerSecurityContextRepository ptcServerSecurityContextRepository;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.securityContextRepository(ptcServerSecurityContextRepository);
        SecurityWebFilterChain chain = http.formLogin()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(serverAuthenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
                .and()
                .cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .authorizeExchange()
                .anyExchange().access(authorizeConfigManager)
                .and().build();

        chain.getWebFilters()
                .filter(webFilter -> webFilter instanceof ExceptionTranslationWebFilter)
                .subscribe(webFilter -> {
                    ExceptionTranslationWebFilter filter = (ExceptionTranslationWebFilter) webFilter;
                    filter.setAccessDeniedHandler(accessDeniedHandler);
                });

        return chain;
    }

}