package com.rhys.eurekaprovider.controller;

import com.rhys.eurekaprovider.service.HealthStatusService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/9/13 11:44 下午
 */
@RestController
public class ProviderController {
    @Value("${server.port}")
    private String port;

    @Resource
    private HealthStatusService healthStatusService;

    @GetMapping("/pingProvider")
    public String ping() {
        return "Ping Provider Success Port:" + port;
    }


    @GetMapping("/health")
    public String health(@RequestParam("status") Boolean status) {
        healthStatusService.setStatus(status);
        return healthStatusService.getStatus();
    }
}
