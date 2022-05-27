package com.ptc.gateway.security;


import com.google.gson.JsonObject;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * author: peter
 */
@Component
public class CustomHttpBasicServerAuthenticationEntryPoint extends HttpBasicServerAuthenticationEntryPoint /* implements ServerAuthenticationEntryPoint */ {


    private static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    private static final String DEFAULT_REALM = "Realm";
    private static String WWW_AUTHENTICATE_FORMAT = "Basic realm=\"%s\"";
    private String headerValue = createHeaderValue("Realm");

    public CustomHttpBasicServerAuthenticationEntryPoint() {
    }


    public void setRealm(String realm) {
        this.headerValue = createHeaderValue(realm);
    }

    private static String createHeaderValue(String realm) {
        Assert.notNull(realm, "realm cannot be null");
        return String.format(WWW_AUTHENTICATE_FORMAT, new Object[]{realm});
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        ServerHttpResponse response = exchange.getResponse();
//        response.setStatusCode(HttpStatus.UNAUTHORIZED);
//        response.getHeaders().set(HttpHeaders.AUTHORIZATION, this.headerValue);
        response.getHeaders().add("Content-Type", "application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        result.addProperty("code", "3001");
        result.addProperty("msg", "not login");
        byte[] dataBytes = result.toString().getBytes();
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(dataBytes);
        return response.writeWith(Mono.just(bodyDataBuffer));
    }
}