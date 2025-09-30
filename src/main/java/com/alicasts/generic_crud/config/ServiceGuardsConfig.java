package com.alicasts.generic_crud.config;

import com.alicasts.generic_crud.repository.UserRepository;
import com.alicasts.generic_crud.service.guard.UserUniquenessGuard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceGuardsConfig {

    @Bean
    public UserUniquenessGuard userUniquenessGuard(UserRepository userRepository) {
        return new UserUniquenessGuard(userRepository);
    }
}
