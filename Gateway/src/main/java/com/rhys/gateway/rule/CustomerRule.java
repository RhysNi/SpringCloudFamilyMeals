package com.rhys.gateway.rule;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.Server;

import java.util.List;
import java.util.Random;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/10/6 4:12 上午
 */
public class CustomerRule extends AbstractLoadBalancerRule {
    @Override
    public Server choose(Object o) {
        List<Server> serverList = getLoadBalancer().getReachableServers();
        //TODO：实现符合项目需要的负载均衡算法
        return serverList.get(new Random().nextInt(serverList.size()));
    }

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        // TODO document why this method is empty
    }
}
