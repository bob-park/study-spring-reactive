package com.hwpark.reactivesecurity.security.configure;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hwpark.reactivesecurity.repository.UserRepository;

@Configuration
public class SecurityConfiguration {

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
        return args -> operations.save(
            new com.hwpark.commons.domain.User("test",
                passwordEncoder.encode("12345"),
                List.of("ROLE_USER")));
    }

}
