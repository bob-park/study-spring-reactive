package com.hwpark.reactivesecurity.security.configure;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.hwpark.reactivesecurity.repository.UserRepository;

@EnableReactiveMethodSecurity // 메서드 보안 설정 시 반드시 추가해줘야 한다.
@Configuration
public class SecurityConfiguration {

    public static final String USER = "USER";
    public static final String INVENTORY = "INVENTORY";

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
                    List.of(role(USER), role(INVENTORY))));
        };
    }

    @Bean
    public SecurityWebFilterChain myCustomSecurityPolicy(ServerHttpSecurity http) {
        return http
            .authorizeExchange(exchanges -> exchanges
                // method 보안 정책으로 인한 주석 처리
//                .pathMatchers(HttpMethod.POST, "/").hasRole(INVENTORY)
//                .pathMatchers(HttpMethod.DELETE, "/**").hasRole(INVENTORY)
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
