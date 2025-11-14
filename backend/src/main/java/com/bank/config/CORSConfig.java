package com.bank.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class CORSConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials
        config.setAllowCredentials(true);

        // Use origin patterns (more reliable on Render)
        config.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));

        // Allow all headers
        config.setAllowedHeaders(List.of("*"));

        // Allow specific HTTP methods
        config.setAllowedMethods(Collections.singletonList("*"));

        // Expose headers
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Max age for preflight requests
        config.setMaxAge(3600L);

        // Register CORS configuration for all endpoints
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }


}