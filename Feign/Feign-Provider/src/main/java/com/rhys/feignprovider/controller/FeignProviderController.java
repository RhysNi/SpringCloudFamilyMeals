package com.rhys.feignprovider.controller;

import com.rhys.serviceapi.Service.ServiceApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/9/21 1:34 上午
 */
@RestController
public class FeignProviderController implements ServiceApi {
    private AtomicInteger requestCount = new AtomicInteger();

    @Value("${server.port}")
    private String port;

    @Override
    public String pingFeignProvider() {
        // try {
        //     System.out.println("开始模拟超时");
        //     TimeUnit.MILLISECONDS.sleep(5000);
        // } catch (InterruptedException e) {
        //     throw new RuntimeException();
        // }
        return "Ping Feign Provider Port:" + port + " Success Count:"+requestCount.incrementAndGet();
    }
}