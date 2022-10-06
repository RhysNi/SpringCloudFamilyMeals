package com.rhys.gateway.config;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.DecimalMin;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/10/6 11:14 上午
 */
@Data
@Validated
public class Config {
    @DecimalMin("0.1")
    private Double permitsPerSecond;
}
