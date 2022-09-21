package com.rhys.eurekaprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/***
 *  仅老版本需要加 @EnableEurekaClient
 * @author Rhys.Ni
 * @date 2022/9/13
 * @return
 */
// @EnableEurekaClient
@SpringBootApplication
public class EurekaProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaProviderApplication.class, args);
    }
}
