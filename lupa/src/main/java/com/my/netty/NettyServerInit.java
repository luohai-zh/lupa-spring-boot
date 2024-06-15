package com.my.netty;


import com.my.netty.core.cache.ClientCache;
import com.my.netty.core.dto.ClientDTO;
import com.my.netty.core.dto.InitParamDTO;
import com.my.netty.core.server.IServer;
import com.my.netty.core.service.IDataService;
import com.my.netty.mqtt.MqttServer;
import com.my.netty.websocket.WebSocketServer;
import com.my.netty.websocket.service.WebSocketDataServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;

/**
 * @author: liyang
 * @date: 2020-11-04 14:28
 * @description: netty启动服务
 **/
@Slf4j
@Component
public class NettyServerInit {
    @Resource(name = "mqttDataServiceImpl")
    private IDataService mqttService;
    @Resource(name = "webSocketDataServiceImpl")
    private WebSocketDataServiceImpl webSocketDataService;

    @PostConstruct
    public void init(){
        // 简单版测试服务
//        TestServer testServer = new TestServer();
//        testServer.run();

        // 创建mqtt服务
        MqttServer mqttServer = new MqttServer(InitParamDTO.base(9001, 1, 4, mqttService,new ClientCache()));
        // 创建websocket服务
//        WebSocketServer wsServer = new WebSocketServer(InitParamDTO.base(9010, 1, 4, webSocketDataService, new ClientCache()));
        // 启动服务
        IServer mqttRun = mqttServer.run();
//        IServer wsRun = wsServer.run();

        // todo 测试代码-测试监听服务客户端状态
        Thread thread = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (true) {
                    Thread.sleep(1500);
                    Collection<ClientDTO> clientList = mqttRun.getClientAllList();
                    log.info("mqtt客户端连接数:{}:{}", clientList.size());

//                    Collection<ClientDTO> clientAllList = wsRun.getClientAllList();
//                    log.info("ws客户端连接数:{}:{}", clientAllList.size());
                }
            }
        });
        thread.run();

    }
}
