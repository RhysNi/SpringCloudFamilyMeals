package com.rhys.gateway.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/10/6 4:51 上午
 */
@Component
public class CustomerRoute {
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route(predicateSpec -> predicateSpec.path("/jump")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec.stripPrefix(1))
                        .uri("http://www.baidu.com/"))
                .build();
    }
}
