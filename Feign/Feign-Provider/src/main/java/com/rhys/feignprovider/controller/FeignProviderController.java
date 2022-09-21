package com.rhys.feignprovider.controller;

import com.rhys.serviceapi.Service.ServiceApi;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/9/21 1:34 上午
 */
@RestController
public class FeignProviderController implements ServiceApi {

    @Override
    public String pingFeignProvider() {
        return "Ping Feign Provider Success";
    }
}
