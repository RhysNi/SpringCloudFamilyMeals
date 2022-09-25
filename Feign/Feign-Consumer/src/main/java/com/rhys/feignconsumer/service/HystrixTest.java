package com.rhys.feignconsumer.service;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/9/26 1:06 上午
 */
public class HystrixTest extends HystrixCommand {
    public static void main(String[] args) {
        Future<String> future = new HystrixTest(HystrixCommandGroupKey.Factory.asKey("doProcess")).queue();
        String reslut = "";
        try {
            reslut = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("reslut:" + reslut);
    }


    @Override
    protected Object run() throws Exception {
        System.out.println("开始执行...");
        int i = 1 / 0;
        return "执行成功...";
    }

    /**
     * 备用逻辑
     *
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2022/9/26
     */
    @Override
    protected Object getFallback() {
        return "异常了，走到了getFallback逻辑";
    }

    public HystrixTest(HystrixCommandGroupKey group) {
        super(group);
    }

    public HystrixTest(HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool) {
        super(group, threadPool);
    }

    public HystrixTest(HystrixCommandGroupKey group, int executionIsolationThreadTimeoutInMilliseconds) {
        super(group, executionIsolationThreadTimeoutInMilliseconds);
    }

    public HystrixTest(HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool, int executionIsolationThreadTimeoutInMilliseconds) {
        super(group, threadPool, executionIsolationThreadTimeoutInMilliseconds);
    }

    public HystrixTest(Setter setter) {
        super(setter);
    }
}
