package com.rhys.feignconsumer.controller;

import com.rhys.feignconsumer.service.FeignConsumerApi;
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

    @GetMapping("/testOpenFeign")
    public String testOpenFeign() {
        return feignConsumerApi.pingFeignProvider();
    }
}
