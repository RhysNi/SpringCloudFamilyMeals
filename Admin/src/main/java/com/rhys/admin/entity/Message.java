package com.rhys.admin.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2022/9/29 1:36 上午
 */
@Data
@Builder
public class Message {
    private String msgtype;
    private Content text;
}
