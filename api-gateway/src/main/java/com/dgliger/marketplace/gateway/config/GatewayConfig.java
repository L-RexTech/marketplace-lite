package com.dgliger.marketplace.gateway.config;

import com.dgliger.marketplace.gateway.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // ============================================
                // AUTH SERVICE ROUTES (Port 8081)
                // ============================================

                // Public auth endpoints - NO JWT FILTER (Most specific first!)
                .route("auth-register", r -> r
                        .path("/api/auth/register")
                        .uri("http://localhost:8081"))

                .route("auth-login", r -> r
                        .path("/api/auth/login")
                        .uri("http://localhost:8081"))

                // Protected auth endpoints - WITH JWT FILTER
                .route("auth-me", r -> r
                        .path("/api/auth/me")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:8081"))

                .route("auth-users", r -> r
                        .path("/api/auth/users/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:8081"))

                // Catch-all for any other auth routes (protected)
                .route("auth-protected", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:8081"))

                // ============================================
                // PRODUCT SERVICE ROUTES (Port 8082)
                // ============================================

                // Public product endpoints - GET requests only
                .route("product-list", r -> r
                        .path("/api/products")
                        .and().method("GET")
                        .uri("http://localhost:8082"))

                .route("product-detail", r -> r
                        .path("/api/products/{id}")
                        .and().method("GET")
                        .uri("http://localhost:8082"))

                .route("product-category", r -> r
                        .path("/api/products/category/{category}")
                        .and().method("GET")
                        .uri("http://localhost:8082"))

                .route("product-seller", r -> r
                        .path("/api/products/seller/{sellerId}")
                        .and().method("GET")
                        .uri("http://localhost:8082"))

                // Protected product endpoints - POST, PUT, DELETE
                .route("product-protected", r -> r
                        .path("/api/products/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:8082"))

                // ============================================
                // ORDER SERVICE ROUTES (Port 8083)
                // ============================================

                // All order endpoints are protected
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:8083"))

                .build();
    }
}