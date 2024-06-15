package com.my.netty.websocket.service;

import com.my.netty.core.cache.IServerListCache;
import com.my.netty.core.dto.ClientDTO;
import com.my.netty.core.enums.ServerTypeEnum;
import com.my.netty.core.server.IServer;
import com.my.netty.core.service.AbsDataService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;


/**
 * @author liyang
 * @date 2021/3/25 16:23
 */
@Slf4j
@Service
public class WebSocketDataServiceImpl extends AbsDataService {
    @Override
    public String inputListening(Channel channel, String topic, String inputMessage) {
        log.info("ws来货啦+====》{}",inputMessage);
        IServer iServer = IServerListCache.SERVER_LIST_MAP.get(ServerTypeEnum.WS.TYPE);
        Collection<ClientDTO> clientAllList = iServer.getClientAllList();
        if (!CollectionUtils.isEmpty(clientAllList)) {
            for (ClientDTO clientDTO : clientAllList) {
                Channel channel1 = clientDTO.getChannel();
                channel1.writeAndFlush(new TextWebSocketFrame(inputMessage));
            }
        }
//        channel.writeAndFlush(new TextWebSocketFrame(inputMessage));
        return inputMessage;
    }
}
