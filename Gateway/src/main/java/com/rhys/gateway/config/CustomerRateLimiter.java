package com.rhys.gateway.config;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.AbstractRateLimiter;
import org.springframework.cloud.gateway.support.ConfigurationService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/10/6 11:13 上午
 */
@Primary
@Component
public class CustomerRateLimiter extends AbstractRateLimiter<Config> {

    /**
     * 每秒发一个令牌
     */
    private final RateLimiter limiter = RateLimiter.create(1);

    @Override
    public Mono<Response> isAllowed(String routeId, String id) {
        Config config = getConfig().get(routeId);
        limiter.setRate(Objects.isNull(config.getPermitsPerSecond()) ? 1 : config.getPermitsPerSecond());
        boolean isAllow = limiter.tryAcquire();
        return Mono.just(new Response(isAllow, new HashMap<>()));
    }

    public CustomerRateLimiter() {
        super(Config.class, "default-rate-limit", new ConfigurationService());
    }
}
