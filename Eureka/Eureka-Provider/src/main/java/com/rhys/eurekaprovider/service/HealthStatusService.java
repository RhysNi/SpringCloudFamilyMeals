package com.rhys.eurekaprovider.service;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Service;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/9/16 3:25 上午
 */
@Service
public class HealthStatusService implements HealthIndicator {
    private Boolean status = true;

    public void setStatus(Boolean status) {
        this.status = status;
    }


    @Override
    public Health health() {
        if (status) {
            return new Health.Builder().up().build();
        }
        return new Health.Builder().down().build();
    }

    public String getStatus() {
        return this.status.toString();
    }
}
