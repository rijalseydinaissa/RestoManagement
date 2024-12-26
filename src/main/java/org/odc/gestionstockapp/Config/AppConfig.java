package org.odc.gestionstockapp.Config;

import org.odc.gestionstockapp.Datas.Repositories.UserRepository;
import org.odc.gestionstockapp.Services.Implementation.UserDetailServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {
    private final UserRepository userRepository;
    private final UserDetailServiceImpl userDetailService;

    public AppConfig(UserRepository userRepository, UserDetailServiceImpl userDetailService) {
        this.userRepository = userRepository;
        this.userDetailService = userDetailService;
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailService); // Directly using the injected userDetailService
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}
