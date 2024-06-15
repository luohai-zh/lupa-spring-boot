package com.my.netty.core.dto;

import io.netty.channel.Channel;
import lombok.Data;

import java.util.List;

/**
 * 客户端对象
 * @author liyang
 * @date 2021/3/11 9:58
 */
@Data
public class ClientDTO {
    // 客户端id
    private String id;
    // 对应通道
    private Channel channel;
    // 订阅主题
    private List<String> topic;

    public static ClientDTO build(String id, Channel channel){
        return build(id,channel,null);
    }
    public static ClientDTO build(String id, Channel channel, List<String> topic){
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(id);
        clientDTO.setChannel(channel);
        clientDTO.setTopic(topic);
        return clientDTO;
    }
}
