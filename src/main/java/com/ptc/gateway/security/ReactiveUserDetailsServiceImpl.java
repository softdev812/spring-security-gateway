package com.ptc.gateway.security;

import com.ptc.gateway.feign.UserFeignClient;
import com.ptc.gateway.security.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {

    private static final String USER_NOT_EXISTS = "empty user！";

//    private final UserService userAuthApi;

//    public MySqlReactiveUserDetailsServiceImpl() {
//        userAuthApi = new UserService();
//    }


    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    @Lazy
    private UserFeignClient userFeignClient;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return doFindByUsername(username)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException(USER_NOT_EXISTS))))
                .doOnNext(u -> log.info(
                        String.format("success user:%s password:%s", u.getUsername(), u.getPassword())))
                .cast(UserDetails.class);
    }

    private Mono<UserEntity> doFindByUsername(String username) {
        UserEntity user = userFeignClient.findByUsername(username);
//        if (user != null) {
//            String password = (String) user.get("password");
//            List<String> perms = (List<String>) user.get("authorities");
//            List<GrantedAuthority> authorities = new ArrayList<>();
//            for (String perm: perms) {
//                if (!StringUtils.isEmpty(perm)) {
//                    authorities.add(new SimpleGrantedAuthority(perm));
//                }
//            }
//
//            return Mono.just(new UserEntity(username, password, authorities));
//        }
//        return Mono.empty();
        if (user == null) {
            return Mono.empty();
        } else {
            return Mono.just(user);
        }
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
//        return userAuthApi.findByUsername(user.getUsername())
//                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException(USER_NOT_EXISTS))))
//                .map(foundedUser -> {
//                    foundedUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
//                    return foundedUser;
//                })
//                .flatMap(updatedUser -> userAuthApi.save(updatedUser))
//                .cast(UserDetails.class);
        return Mono.empty();
    }
}