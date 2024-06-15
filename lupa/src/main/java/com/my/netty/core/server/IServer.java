package com.my.netty.core.server;

import com.my.netty.core.dto.ClientDTO;
import com.my.netty.core.dto.InitParamDTO;
import com.my.netty.core.enums.ServerRunStatusEnum;

import java.util.Collection;
import java.util.List;


/**
 * 服务接口
 * @author liyang
 * @date 2021/3/10 17:35
 */
public interface IServer {
    /**
     * 启动服务
     *
     * @return
     * @author liyang
     * @date 2021/3/11 10:21
     */
    IServer run ();

    /**
     * 运行状态
     *
     * @param
     * @return
     * @author liyang
     * @date 2021/3/11 11:02
     */
    ServerRunStatusEnum status();

    /**
     * 根据主题发送消息
     *
     * @param
     * @return
     * @author liyang
     * @date 2021/3/11 10:22
     */
    void sendAll(String topic, String sendMessage);

    /**
     * 获取所有客户端列表
     *
     * @param
     * @return
     * @author liyang
     * @date 2021/3/11 10:22
     */
    Collection<ClientDTO> getClientAllList();

    /**
     * 获取客户端列表，根据主题
     *
     * @param
     * @return
     * @author liyang
     * @date 2021/3/11 10:22
     */
    List<ClientDTO> getClientListByTopic(String... topic);

}
