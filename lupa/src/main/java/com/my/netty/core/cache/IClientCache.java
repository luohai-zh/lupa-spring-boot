package com.my.netty.core.cache;

import com.my.netty.core.dto.ClientDTO;
import io.netty.channel.Channel;

import java.util.Collection;


/**
 * 客户端缓存
 * @author liyang
 * @date 2021/3/11 10:41
 */
public interface IClientCache {
    Collection<ClientDTO> getAllList();
    // 客户端总数量
    int count();
    // 添加客户端
    void add(Channel channel,String message);
    // 删除客户端
    void remove(Channel channel,String message);
    // 提取客户端
    ClientDTO get(String clientKey);
}
