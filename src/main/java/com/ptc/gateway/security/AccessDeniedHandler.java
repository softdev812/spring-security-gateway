package com.ptc.gateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class AccessDeniedHandler implements ServerAccessDeniedHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        ServerHttpResponse response = exchange.getResponse();
        //setting headers
        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        httpHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        httpHeaders.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization");

        DataBufferFactory dataBufferFactory = response.bufferFactory();
        Map<String, Object> m = new HashMap<>();
        m.put("code", 1);
        m.put("msg", "has no permission");
        ObjectMapper o = new ObjectMapper();
        try {
            String s = o.writeValueAsString(m);
            return response.writeWith(Mono.just(dataBufferFactory.wrap(s.getBytes())));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return response.writeWith(Mono.just(dataBufferFactory.wrap("error permission".getBytes())));
    }
}