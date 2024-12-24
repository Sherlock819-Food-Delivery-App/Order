// package com.example.Order.config.auth;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.filter.CorsFilter;

// @Configuration
// public class CorsConfig {

//     @Bean
//     public CorsFilter corsFilter() {
//         CorsConfiguration corsConfiguration = new CorsConfiguration();
//         corsConfiguration.addAllowedOrigin("http://localhost:3007"); // Add your allowed origin
//         corsConfiguration.addAllowedHeader("*");
//         corsConfiguration.addAllowedMethod("*");
//         corsConfiguration.setAllowCredentials(true);

//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", corsConfiguration);

//         return new CorsFilter(source);
//     }
// }

