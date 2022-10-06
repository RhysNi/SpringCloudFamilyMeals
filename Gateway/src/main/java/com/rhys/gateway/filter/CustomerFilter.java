package com.rhys.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/10/6 4:51 上午
 */
@Component
public class CustomerFilter implements Ordered, GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        RequestPath path = exchange.getRequest().getPath();
        if ("/jump".equals(String.valueOf(path))) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().setRawStatusCode(HttpStatus.UNAUTHORIZED.value());
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap("被过滤器处理过了，跳转不了百度了".getBytes(StandardCharsets.UTF_8))));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
