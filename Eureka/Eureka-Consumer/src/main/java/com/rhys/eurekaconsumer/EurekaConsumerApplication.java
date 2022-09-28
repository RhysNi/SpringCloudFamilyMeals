package com.rhys.eurekaconsumer;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author RhysNi
 */
@SpringBootApplication
public class EurekaConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaConsumerApplication.class, args);
    }

    /**
     * `@LoadBalanced`:开启负载均衡
     * @author Rhys.Ni
     * @date 2022/9/19
     * @return org.springframework.web.client.RestTemplate
     */
    @Bean
    // @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 修改负载均衡策略
     * @author Rhys.Ni
     * @date 2022/9/19
     * @return com.netflix.loadbalancer.IRule
     */
    // @Bean
    // public IRule myRule() {
    //     // return new RoundRobinRule();
    //     // return new RetryRule();
    //     return new RandomRule();
    // }
}
