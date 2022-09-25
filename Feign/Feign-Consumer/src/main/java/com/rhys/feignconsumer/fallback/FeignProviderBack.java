package com.rhys.feignconsumer.fallback;

import com.rhys.feignconsumer.service.FeignConsumerApi;
import org.springframework.stereotype.Component;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/9/26 1:30 上午
 */
@Component
public class FeignProviderBack implements FeignConsumerApi {

    @Override
    public String pingFeignProvider() {
        return "降级了,返回了兜底数据";
    }
}
