package com.ptc.gateway.security;

import com.ptc.common.auth.AccessTokenService;
import com.ptc.common.auth.UserDetail;
import com.ptc.gateway.feign.RedisFeignClient;
import com.ptc.gateway.security.entity.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * author: peter
 */
@Component
public class AuthenticationSuccessHandler extends WebFilterChainServerAuthenticationSuccessHandler {

    @Autowired
    private RedisFeignClient redisFeignClient;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        System.out.println("success");
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpResponse response = exchange.getResponse();

        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        httpHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        httpHeaders.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization");

        Map<String, Object> res = new HashMap<>();
        byte[] dataBytes = {};
        ObjectMapper mapper = new ObjectMapper();
        try {
            String token = UUID.randomUUID().toString().replaceAll("-", "");
            httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
            res.put("code", 0);
            res.put("msg", "success login");
            res.put("token", token);
            res.put("userInfo", authentication.getPrincipal());
            res.put("data", "data");

            UserEntity userEntity = (UserEntity) authentication.getPrincipal();
            UserDetail u = new UserDetail();
            u.setRights(userEntity.getRights());
            u.setUsername(userEntity.getUsername());
            u.setPassword(userEntity.getPassword());
            u.setId(userEntity.getUserId());

            redisFeignClient.set(AccessTokenService.AT_GROUP + ":" + token, mapper.writeValueAsString(u), AccessTokenService.PC_AT_EXPIRES_HOUR * 60 * 60 * 12);
            dataBytes = mapper.writeValueAsBytes(res);
        } catch (Exception ex) {
            ex.printStackTrace();
            JsonObject result = new JsonObject();
            result.addProperty("status", "0");
            result.addProperty("message", "invalid permission");
            dataBytes = result.toString().getBytes();
        }
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(dataBytes);
        return response.writeWith(Mono.just(bodyDataBuffer));
    }

    private String authListToString(Collection<? extends GrantedAuthority> collection) {
        StringBuilder auth = new StringBuilder();
        for (GrantedAuthority authority : collection) {
            auth.append(authority.getAuthority()).append(",");
        }
        return auth.toString();

    }

}
