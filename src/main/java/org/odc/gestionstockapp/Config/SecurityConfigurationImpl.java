package org.odc.gestionstockapp.Config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfigurationImpl {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthFilter jwtAuthenticationFilter;

    public SecurityConfigurationImpl(
            JwtAuthFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()

                .requestMatchers(
                        "/auth/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/error/**",
                        "/webjars/**",
                        "/api-docs/**",
                        "/commandes/{id}/status",
                        "/factures/generer/{commandeId}",
                        "/api/factures/generer/{commandeId}",
                        ("/telecharger-facture/{urlUnique}"),
                        "/factures/telecharger-facture/{urlUnique}",
                        "/generer/{commandeId}",
                        "/telecharger/{urlUnique}"
                )
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.cors(); // Activer la configuration CORS

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Spécifier explicitement les origines autorisées
        configuration.setAllowedOrigins(List.of(
                "http://localhost:4200",      // Application Angular en développement
                "http://localhost:8080",
                "http://localhost:8081",
                "https://angular-front-app-mu.vercel.app"
        ));

        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS",
                "PATCH"
        ));
        // Headers autorisés
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Cache-Control"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Cache la configuration CORS pendant 1 heure
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}