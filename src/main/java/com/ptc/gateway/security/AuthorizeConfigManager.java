package com.ptc.gateway.security;

import com.ptc.gateway.security.auth.PcAuthManager;
import com.ptc.gateway.security.auth.WxAuthManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuthorizeConfigManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Value("${ptc.system.auth-white-list}")
    private String[] AUTH_WHITELIST;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication,
                                             AuthorizationContext authorizationContext) {
        ServerWebExchange exchange = authorizationContext.getExchange();

        String requestPath = exchange.getRequest().getURI().getPath();

        for (String s : AUTH_WHITELIST) {
            if (antPathMatcher.match(s, requestPath)) {
                return Mono.just(new AuthorizationDecision(true));
            }
        }

        return authentication.map(auth ->
            new AuthorizationDecision(checkAuthorities(auth, requestPath))
        ).defaultIfEmpty(new AuthorizationDecision(false));
    }

    private boolean checkAuthorities(Authentication auth, String requestPath) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        if (requestPath.startsWith("/xxx")) {
            return WxAuthManager.getInstance().checkAuth(requestPath, auth);
        }
        return PcAuthManager.getInstance().checkAuth(requestPath, authorities.stream().map(item -> item.getAuthority()).collect(Collectors.toList()));
    }
}
