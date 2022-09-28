package com.rhys.admin.notifier;

import com.alibaba.fastjson.JSONObject;
import com.rhys.admin.sender.DingDingMessageSender;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.notify.AbstractStatusChangeNotifier;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/9/29 1:31 上午
 */
public class DingDingNotifier extends AbstractStatusChangeNotifier {

    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        String serviceName = instance.getRegistration().getName();
        String serviceUrl = instance.getRegistration().getServiceUrl();
        String status = instance.getStatusInfo().getStatus();
        Map<String, Object> details = instance.getStatusInfo().getDetails();
        return Mono.fromRunnable(() -> DingDingMessageSender.sendTextMessage("系统服务告警 : 【" + serviceName + "】" + "【服务地址】" + serviceUrl + "【状态】" + status + "【详情】" + JSONObject.toJSONString(details)));
    }

    public DingDingNotifier(InstanceRepository repository) {
        super(repository);
    }
}
