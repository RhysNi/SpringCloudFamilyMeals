package com.rhys.serviceapi.Service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/9/21 4:17 上午
 */
@RequestMapping("/serviceApi")
public interface ServiceApi {

    @GetMapping("/pingFeignProvider")
    String pingFeignProvider();
}
