package com.rhys.eurekaconsumer.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.rhys.eurekaconsumer.service.HealthStatusService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/9/13 11:44 下午
 */
@RestController
@RequestMapping("/consumer")
public class ConsumerController {
    /**
     * 导入org.springframework.cloud.client.discovery.DiscoveryClient （Spring官方指定的标准）
     */
    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private EurekaClient eurekaClient;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private LoadBalancerClient loadBalancer;

    @Resource
    private HealthStatusService healthStatusService;

    @Value("${eureka.instance.hostname}")
    private String hostName;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 心跳接口
     *
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2022/9/15
     */
    @GetMapping("/ping")
    public String ping() {
        return "ping success";
    }


    /**
     * 获取服务列表
     *
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2022/9/15
     */
    @GetMapping("/services")
    public String getServices() {
        List<String> services = discoveryClient.getServices();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[ ");
        for (String service : services) {
            stringBuffer.append(service + " ");
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }

    /**
     * 获取应用实例信息
     *
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2022/9/15
     */
    @GetMapping("/instances")
    public Object getInstances() {
        return discoveryClient.getInstances("EurekaProvider");
    }


    /**
     * 获取应用实例信息
     *
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2022/9/15
     */
    @GetMapping("/testProvider")
    public Object pingProvider() {
        //根据服务名找注册列表
        List<InstanceInfo> instances = eurekaClient.getInstancesByVipAddress("EurekaProvider", false);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[ ");
        for (InstanceInfo instanceInfo : instances) {
            stringBuffer.append(instanceInfo + " ");
        }
        stringBuffer.append("]");
        System.out.println(stringBuffer);

        if (instances.size() > 0) {
            InstanceInfo instanceInfo = instances.get(0);
            //如果服务处于活跃状态下则取URL
            if (instanceInfo.getStatus().equals(InstanceInfo.InstanceStatus.UP)) {
                String url = "http://" + instanceInfo.getHostName() + ":" + instanceInfo.getPort() + "/pingProvider";
                ResponseEntity<String> responseEntity = new RestTemplate().getForEntity(url, String.class);
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    String result = responseEntity.getBody();
                    return result;
                } else {
                    return "Bad Request";
                }
            }
        }
        return "Bad Request";
    }

    /**
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2022/9/15
     */
    @GetMapping("/testProviderForLB")
    public Object testProviderForLB() {
        String url = "http://EurekaProvider/pingProvider";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String result = responseEntity.getBody();
            return result;
        } else {
            return "Bad Request";
        }
    }


    /**
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2022/9/15
     */
    @GetMapping("/testCustomerLB")
    public Object testCustomerLB() {
        //获取所有节点信息
        List<ServiceInstance> instances = discoveryClient.getInstances("EurekaProvider");

        //随机
        int nextInt = new Random().nextInt(instances.size());
        ServiceInstance instance = instances.get(nextInt);

        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/pingProvider";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String result = responseEntity.getBody();
            return result;
        } else {
            return "Bad Request";
        }
    }

    @GetMapping("/testLBConfig")
    public Object testLBConfig() {
        //获取所有节点信息
        ServiceInstance instance = loadBalancer.choose("EurekaProvider");
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/pingProvider";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String result = responseEntity.getBody();
            return result;
        } else {
            return "Bad Request";
        }
    }

    @GetMapping("/health")
    public String health(@RequestParam("status") Boolean status) {
        healthStatusService.setStatus(status);
        return healthStatusService.getStatus();
    }
}
