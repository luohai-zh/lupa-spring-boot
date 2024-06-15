package com.my.netty.core.cache;

import com.my.netty.core.enums.ServerTypeEnum;
import com.my.netty.core.server.IServer;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务列表缓存
 * @author liyang
 * @date 2021/3/10 17:37
 */
public interface IServerListCache {
    // 运行中服务列表
    Map<String, IServer> SERVER_LIST_MAP = new HashMap<>();

    static IServer getServer(ServerTypeEnum serverTypeEnum){
        return SERVER_LIST_MAP.get(serverTypeEnum.TYPE);
    }
}
