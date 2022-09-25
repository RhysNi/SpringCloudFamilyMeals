package com.rhys.feignconsumer.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/9/26 2:44 上午
 */
@Service
public class TestRestService {

    @Resource
    private RestTemplate restTemplate;

    @HystrixCommand(defaultFallback = "testFallBack")
    public String testOpenFeignWithRest() {
        String url = "http://FeignProvider/pingFeignProvider";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String result = responseEntity.getBody();
            return result;
        } else {
            return "Bad Request";
        }
    }

    private String testFallBack() {
        return "@HystrixCommand 实现了降级，返回了兜底数据";
    }
}
