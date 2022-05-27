package com.ptc.gateway.security;

import com.ptc.common.auth.UserDetail;
import com.ptc.gateway.feign.RedisFeignClient;
import com.ptc.gateway.feign.UserFeignClient;
import com.ptc.gateway.security.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * author: peter
 */
@Slf4j
@Component
public class ServerSecurityContextRepository implements ServerSecurityContextRepository {

    @Autowired
    private RedisFeignClient redisFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();

    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {

        HttpHeaders headers = exchange.getRequest().getHeaders();
        String token = headers.getFirst("ACCESS-TOKEN");

        // INVALID TOKEN
        if (token == null) {
            if (headers.getFirst("cookie") != null) {
                String[] list = headers.getFirst("cookie").split("ACCESS-TOKEN=");
                if (list == null && list.length <= 1) {
                    return Mono.empty();
                } else {
                    token = list[1];
                }
            } else {
                return Mono.empty();
            }
        }

//        R r = redisDemoFeignClient.checkToken(token);
//        String userVoString = redisDemoFeignClient.get(AccessTokenService.AT_GROUP + ":" + token);
        UserDetail userDetail = userFeignClient.getByToken(token);

        if (userDetail == null) {
            return Mono.empty();
        }

        userFeignClient.refreshToken(token);
        //redisFeignClient.expire(AccessTokenService.AT_GROUP + ":" + token,AccessTokenService.PC_AT_EXPIRES_HOUR * 60 * 60 * 12);


        UserEntity userEntity = new UserEntity();
        userEntity.setRights(userDetail.getRights());
        userEntity.setUserId(userDetail.getId());
        userEntity.setPassword(userDetail.getPassword());
        userEntity.setUserName(userDetail.getUsername());

        List<SimpleGrantedAuthority> collect = userEntity.getRights().stream().filter(item -> !item.isEmpty()).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userEntity.getUsername(), userEntity.getPassword(), collect);
        SecurityContext context = new SecurityContextImpl(authentication);
        return Mono.just(context);
    }
}
