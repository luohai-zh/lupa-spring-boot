package com.my.netty.mqtt;

import com.alibaba.fastjson.JSONObject;
import com.my.netty.core.cache.IServerListCache;
import com.my.netty.core.dto.ClientDTO;
import com.my.netty.core.dto.InitParamDTO;
import com.my.netty.core.enums.ServerRunStatusEnum;
import com.my.netty.core.enums.ServerTypeEnum;
import com.my.netty.core.server.AbsServer;
import com.my.netty.core.server.IServer;
import com.my.netty.mqtt.handler.MqttHandler;
import com.my.netty.mqtt.handler.MqttRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: liyang
 * @date: 2020-11-04 14:28
 * @description: netty服务
 **/
@Slf4j
public class MqttServer extends AbsServer {

    public IServer run (){
        if (initParamDTO == null) {
            throw new RuntimeException("启动参数错误:"+ JSONObject.toJSONString(initParamDTO));
        }
        log.info("netty服务启动中...线程数-boss:{},work:{}",initParamDTO.getBoosTread(),initParamDTO.getWorkTread());
        // 构建主线程-用于分发socket请求
        EventLoopGroup boosGroup = new NioEventLoopGroup(initParamDTO.getBoosTread());
        // 构建工作线程-用于处理请求处理
        EventLoopGroup workGroup = new NioEventLoopGroup(initParamDTO.getWorkTread());
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boosGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
//                    .childOption(ChannelOption.SO_BACKLOG,1024)     //等待队列
                    .childOption(ChannelOption.SO_REUSEADDR,true)   //快速复用
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 这个地方注意，如果客户端发送请求体超过此设置值，会抛异常
                            socketChannel.pipeline().addLast(new MyMqttDecoder(1024*1024));
                            socketChannel.pipeline().addLast( MqttEncoder.INSTANCE);
                            // 加载MQTT编解码协议，包含业务逻辑对象
                            socketChannel.pipeline().addLast("mqttHandler",new MqttHandler(initParamDTO));
                        }
                    });
            serverBootstrap.bind(initParamDTO.getPort()).addListener(future -> {
                log.info("服务端成功绑定端口号={}",initParamDTO.getPort());
            });

        }catch (Exception e){
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            log.error("mqttServer启动失败:{}",e);
        }
        // 将已启动的服务添加到 服务列表，便于以后管理使用
        IServerListCache.SERVER_LIST_MAP.put(ServerTypeEnum.MQTT.TYPE,this);
        return this;
    }

    public ChannelFuture send(Channel channel, String topic,String sendMessage ) throws InterruptedException {
        MqttRequest request = new MqttRequest((sendMessage.getBytes()));
        MqttPublishMessage pubMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBLISH,
                        request.isDup(),
                        request.getQos(),
                        request.isRetained(),
                        0),
                new MqttPublishVariableHeader(topic, 0),
                Unpooled.buffer().writeBytes(request.getPayload()));
        // 超过高水位，则采取同步模式
        if (channel.isWritable()) {
            return channel.writeAndFlush(pubMessage);
        }
        return channel.writeAndFlush(pubMessage).sync();
    }
    public void sendAll(String topic, String sendMessage){
        if (initParamDTO.getClientCache() == null) {
            return;
        }
        if(initParamDTO.getClientCache().count()>0){
            for (ClientDTO clientDTO : initParamDTO.getClientCache().getAllList()) {
                try {
                    send(clientDTO.getChannel(),topic,sendMessage);
                }catch (Exception e){
                    log.error("主题:{}，推送用户{}失败",topic,clientDTO.getChannel().id());
                }
            }
        }
    }

    @Override
    public Collection<ClientDTO> getClientAllList() {
        if (initParamDTO.getClientCache() == null || CollectionUtils.isEmpty(initParamDTO.getClientCache().getAllList())){
            return new ArrayList<>();
        }
        return initParamDTO.getClientCache().getAllList();
    }

    @Override
    public List<ClientDTO> getClientListByTopic(String... topic) {
        return null;
    }

    @Override
    public ServerRunStatusEnum status() {
        return null;
    }

    public MqttServer(InitParamDTO initParamDTO) {
        this.initParamDTO = initParamDTO;
    }

    public MqttServer() {
    }
}
