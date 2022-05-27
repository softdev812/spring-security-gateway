package com.ptc.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractUserDetailsReactiveAuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Component
public class AuthenticationManager extends AbstractUserDetailsReactiveAuthenticationManager {

    private Scheduler scheduler = Schedulers.boundedElastic();

    //    private PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReactiveUserDetailsServiceImpl mySqlReactiveUserDetailsService;


    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        final String username = authentication.getName();
        final String presentedPassword = (String) authentication.getCredentials();
        return retrieveUser(username)
                .publishOn(scheduler)
                .filter(u -> passwordEncoder.matches(presentedPassword, u.getPassword()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid Credentials"))))
                .flatMap(u -> {
                    boolean upgradeEncoding = mySqlReactiveUserDetailsService != null
                            && passwordEncoder.upgradeEncoding(u.getPassword());
                    if (upgradeEncoding) {
                        String newPassword = passwordEncoder.encode(presentedPassword);
                        return mySqlReactiveUserDetailsService.updatePassword(u, newPassword);
                    }
                    return Mono.just(u);
                })
                .flatMap(userDetails -> {
                    return Mono.just(userDetails);
                })
                .map(u -> new UsernamePasswordAuthenticationToken(u, u.getPassword(), u.getAuthorities()));
    }

    @Override
    protected Mono<UserDetails> retrieveUser(String username) {
        return mySqlReactiveUserDetailsService.findByUsername(username);
    }
}