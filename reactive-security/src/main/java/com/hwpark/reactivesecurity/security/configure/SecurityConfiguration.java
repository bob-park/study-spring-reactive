package com.hwpark.reactivesecurity.security.configure;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.hwpark.reactivesecurity.repository.UserRepository;

@Configuration
public class SecurityConfiguration {

    private static final String USER = "USER";
    private static final String INVENTORY = "INVENTORY";

    public static String role(String auth) {
        return "ROLE_" + auth;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository userRepository,
        PasswordEncoder passwordEncoder) {
        return username -> userRepository.findByName(username)
            .map(user -> User.withUsername(user.getName())
                .password(user.getPassword())
                .authorities(user.getRoles().toArray(new String[0]))
                .build()
            );
    }

    @Bean
    public CommandLineRunner userLoader(MongoOperations operations,
        PasswordEncoder passwordEncoder) {
        return args -> {
            operations.save(
                new com.hwpark.commons.domain.User("user",
                    passwordEncoder.encode("12345"),
                    List.of(role(USER))));

            operations.save(
                new com.hwpark.commons.domain.User("manager",
                    passwordEncoder.encode("12345"),
                    List.of(role(INVENTORY))));
        };
    }

    @Bean
    public SecurityWebFilterChain myCustomSecurityPolicy(ServerHttpSecurity http) {
        return http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(HttpMethod.POST, "/").hasRole(INVENTORY)
                .pathMatchers(HttpMethod.DELETE, "/**").hasRole(INVENTORY)
                .anyExchange().authenticated()
                .and()
                .httpBasic()
                .and()
                .formLogin()
            )
            .csrf().disable()
            .build();
    }

}
