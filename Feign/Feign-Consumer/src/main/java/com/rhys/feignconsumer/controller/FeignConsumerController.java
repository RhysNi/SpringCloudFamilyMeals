package com.rhys.feignconsumer.controller;

import com.rhys.feignconsumer.service.FeignConsumerApi;
import com.rhys.feignconsumer.service.TestRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/9/21 2:50 上午
 */
@RestController
public class FeignConsumerController {
    @Resource
    private FeignConsumerApi feignConsumerApi;

    @Resource
    private TestRestService restService;

    @Value("${server.port}")
    private String port;

    @GetMapping("/testOpenFeign")
    public String testOpenFeign() {
        return "Consumer:" + port + "-" + feignConsumerApi.pingFeignProvider();
    }

    @GetMapping("/testOpenFeignWithRest")
    public Object testOpenFeignWithRest() {
        return restService.testOpenFeignWithRest();
    }
}
