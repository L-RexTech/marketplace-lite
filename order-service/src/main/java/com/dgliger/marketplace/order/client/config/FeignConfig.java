package com.dgliger.marketplace.order.client.config;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attributes =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (attributes != null) {
                    String authHeader = attributes.getRequest().getHeader("Authorization");
                    if (authHeader != null) {
                        template.header("Authorization", authHeader);
                    }

                    // Propagate user context headers
                    String userId = attributes.getRequest().getHeader("X-User-Id");
                    String userEmail = attributes.getRequest().getHeader("X-User-Email");
                    String userRoles = attributes.getRequest().getHeader("X-User-Roles");

                    if (userId != null) template.header("X-User-Id", userId);
                    if (userEmail != null) template.header("X-User-Email", userEmail);
                    if (userRoles != null) template.header("X-User-Roles", userRoles);
                }
            }
        };
    }
}