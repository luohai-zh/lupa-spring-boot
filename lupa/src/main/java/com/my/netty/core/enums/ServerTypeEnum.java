package com.my.netty.core.enums;


/**
 * 协议类型
 *
 * @author liyang
 * @date 2021/3/9 11:22
 */
public enum ServerTypeEnum {
    MQTT("MQTT"),
    WS("WS"),
    ;
    public String TYPE;

    ServerTypeEnum(String type) {
        this.TYPE = type;
    }

}
