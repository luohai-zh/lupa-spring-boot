package com.my.netty.mqtt.service;

import com.my.netty.core.service.AbsDataService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * MQTT服务业务实现对象
 * @author liyang
 * @date 2021/3/10 16:40
 */
@Slf4j
@Service
public class MqttDataServiceImpl extends AbsDataService {
    @Override
    public String inputListening(Channel channel, String topic, String inputMessage) {
        log.info("MQTT监听到了数据:{}",inputMessage);
        super.pushTopicAll(inputMessage,topic);
        log.info("MQTT返回的数据:{]",inputMessage);
        return inputMessage;
    }

}
