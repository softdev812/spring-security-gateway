package com.ptc.gateway.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@SuppressWarnings("serial")
@Getter
@Setter
public class AuthenticationToken extends UsernamePasswordAuthenticationToken {

    private String tenant;

    private String host;

    public AuthenticationToken(Object principal, Object credentials, String tenant, String host) {
        super(principal, credentials);
        this.tenant = tenant;
        this.host = host;
    }

    public AuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public AuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}