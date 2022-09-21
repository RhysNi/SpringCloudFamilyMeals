package com.rhys.feignconsumer.service;

import com.rhys.serviceapi.Service.ServiceApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/9/21 2:52 上午
 */
@FeignClient(name = "FeignProvider")
public interface FeignConsumerApi extends ServiceApi {
}
