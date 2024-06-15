package com.my.netty.core.service;

import com.my.netty.core.cache.IServerListCache;
import com.my.netty.core.enums.ServerTypeEnum;
import io.netty.channel.Channel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务逻辑父类
 * @author liyang
 * @date 2021/3/10 17:05
 */
@Slf4j
@Setter
public abstract class AbsDataService implements IDataService {
    @Override
    public void push(String userOnlyCode, String message) {
    }

    @Override
    public void pushTopicAll(String message,String... topics ) {
        for (String topic: topics) {
            IServerListCache.getServer(ServerTypeEnum.MQTT).sendAll(topic,message);
        }
    }

    @Override
    public void pushAll(String message) {

    }
}
