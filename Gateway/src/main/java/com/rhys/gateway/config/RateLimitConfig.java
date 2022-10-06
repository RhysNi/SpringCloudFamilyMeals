package com.rhys.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/10/6 10:16 上午
 */
@Component
public class RateLimitConfig {
    @Bean
    public KeyResolver keyResolver() {
        return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getQueryParams().getFirst("name")));
    }
}
