package com.my.netty.core.cache;

import com.my.netty.core.dto.ClientDTO;
import io.netty.channel.Channel;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liyang
 * @date 2021/3/11 11:51
 */
public class ClientCache implements IClientCache{
    // 客户端id:客户端
    public ConcurrentHashMap<String, ClientDTO> channels = new ConcurrentHashMap<>();

    @Override
    public Collection<ClientDTO> getAllList() {
        return channels.values();
    }

    @Override
    public int count() {
        return channels.size();
    }

    @Override
    public void add(Channel channel,String message) {
        channels.put(channel.id().asShortText(),ClientDTO.build(channel.id().asShortText(),channel));
    }

    @Override
    public void remove(Channel channel, String message) {
        channels.remove(channel.id().asShortText(),ClientDTO.build(channel.id().asShortText(),channel));
    }

    @Override
    public ClientDTO get(String clientKey) {
        return null;
    }
}
