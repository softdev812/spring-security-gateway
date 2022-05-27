package com.ptc.gateway.security;

import com.ptc.gateway.feign.RedisFeignClient;
import com.ptc.gateway.feign.UserFeignClient;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class AuthenticationConverter implements ServerAuthenticationConverter {

    private String usernameParameter = "username";

    private String passwordParameter = "password";

    @Autowired
    private RedisFeignClient redisFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;


    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {

        String username = exchange.getRequest().getQueryParams().getFirst("username");
        String password = exchange.getRequest().getQueryParams().getFirst("password");
        String captcha = exchange.getRequest().getQueryParams().getFirst("captcha");
        String uuid = exchange.getRequest().getQueryParams().getFirst("uuid");

        Map<String, String> verifyMap = new HashedMap();
        verifyMap.put("captcha", captcha);
        verifyMap.put("uuid", uuid);
        if (!userFeignClient.verify(verifyMap)) {

            return Mono.error(new UsernameNotFoundException("XXX！"));
        }

        return Mono.just(new UsernamePasswordAuthenticationToken(username, password));

    }


    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        Flux<DataBuffer> body = serverHttpRequest.getBody();

        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        return bodyRef.get();
    }

}