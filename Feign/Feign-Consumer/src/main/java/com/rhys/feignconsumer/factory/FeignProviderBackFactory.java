package com.rhys.feignconsumer.factory;

import com.rhys.feignconsumer.service.FeignConsumerApi;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/9/26 2:12 上午
 */
@Component
public class FeignProviderBackFactory implements FallbackFactory<FeignConsumerApi> {
    @Override
    public FeignConsumerApi create(Throwable throwable) {
        return new FeignConsumerApi() {
            @Override
            public String pingFeignProvider() {
                if (throwable instanceof RuntimeException) {
                    return "请求时异常：" + throwable;
                } else {
                    return "FallbackFactory 实现降级了,返回了兜底数据";
                }
            }
        };
    }
}
