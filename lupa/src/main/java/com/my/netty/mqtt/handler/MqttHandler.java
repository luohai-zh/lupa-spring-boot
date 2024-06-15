package com.my.netty.mqtt.handler;

import cn.hutool.core.text.StrSplitter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lupa.entity.Device;
import com.lupa.entity.Historydata;
import com.lupa.entity.Newdata;
import com.lupa.mapper.DeviceMapper;
import com.lupa.mapper.NewdataMapper;
import com.lupa.service.HistorydataService;
import com.lupa.service.NewdataService;
import com.lupa.utils.SpringUtils;
import com.my.netty.core.dto.ClientDTO;
import com.my.netty.core.dto.InitParamDTO;
import com.my.netty.mqtt.MqttServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static io.netty.handler.codec.mqtt.MqttQoS.AT_LEAST_ONCE;

/**
 * @author: liyang
 * @date: 2020/7/29 13:22
 * @description: MQTT业务类
 */
@Slf4j
@ChannelHandler.Sharable
public class MqttHandler extends ChannelInboundHandlerAdapter {
    private String myDeviceId = null;
    private static final Map<String, Object> msgMap = new HashMap<>();

    //设备表
    private static DeviceMapper deviceMapper;

    static {
        deviceMapper = SpringUtils.getBean(DeviceMapper.class);
    }

    //实时数据表
    private static NewdataMapper newdataMapper;

    static {
        newdataMapper = SpringUtils.getBean(NewdataMapper.class);
    }

    //实时数据的服务类
    private static NewdataService newDataService;

    static {
        newDataService = SpringUtils.getBean(NewdataService.class);
    }

    //历史数据的服务类
    private static HistorydataService historydataService;

    static {
        historydataService = SpringUtils.getBean(HistorydataService.class);
    }

    /*private static final Map<String, Object> msgMap = new HashMap<>();
    private static DeviceMapper deviceMapper;

    static {
        deviceMapper = SpringUtils.getBean(DeviceMapper.class);
    }*/

    private InitParamDTO initParamDTO;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof MqttMessage) {
            Channel channel = ctx.channel();
            MqttMessage message = (MqttMessage) msg;

            MqttMessageType messageType = message.fixedHeader().messageType();
            log.info("MQTT接收到的发送类型===》{}", messageType);

            switch (messageType) {
                // 建立连接
                case CONNECT:
                    try {
                        this.connect(channel, (MqttConnectMessage) message);
                    } catch (Exception e) {
                        //如果用户密码，客户端ID校验不成功，会二次建立CONNECT类型连接
                        //但是没有实际意义
                    }
                    break;
                // 推送数据
                case PUBLISH:
                    this.publish(channel, (MqttPublishMessage) message);
                    break;
                // 订阅主题
                case SUBSCRIBE:
                    this.subscribe(channel, (MqttSubscribeMessage) message);
                    break;
                // 退订主题
                case UNSUBSCRIBE:
                    this.unSubscribe(channel, (MqttUnsubscribeMessage) message);
                    break;
                // 心跳包
                case PINGREQ:
                    this.pingReq(channel, message);
                    break;
                // 断开连接
                case DISCONNECT:
                    this.disConnect(channel, message);
                    break;
                // 确认收到响应报文,用于服务器向客户端推送qos1后，客户端返回服务器的响应
                case PUBACK:
                    this.puback(channel, (MqttPubAckMessage) message);
                    break;
                default:
                    if (log.isDebugEnabled()) {
                        log.debug("Nonsupport server message  type of '{}'.", messageType);
                    }
                    break;
            }
        }
    }


    public void connect(Channel channel, MqttConnectMessage msg) {
        // 客户端登录校验
        Device device = null;//初始化device
        if (initParamDTO.getAuthLoginOnOff()) {
            String userName = msg.payload().userName();
            String password = msg.payload().password();

            device = deviceMapper.selectOne(new QueryWrapper<Device>().eq("deviceid", userName).eq("deviceidsecretkey", password));
            if (device == null) {
                //            if(! initParamDTO.getName().equals(userName) || !initParamDTO.getPassword().equals(password)){
                log.info("账号密码不正确，通道关闭===>{}:{}", userName, password);
                // 向客户端返回账号密码不正确消息，其实客户端有重连机制，可直接关闭
                MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(new MqttFixedHeader(
                                MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, true), null);
                channel.writeAndFlush(okResp);
                disConnect(channel, msg);
                return;
            }
        }

        //客户端ID校验
        if (initParamDTO.getAuthClientIdOnOff()) {
            String clientId = msg.payload().clientIdentifier();
            List<String> clientIdSplit = StrSplitter.split(clientId, '|', 0, true, true);
            System.out.println(clientIdSplit.get(1));
            myDeviceId = clientIdSplit.get(1);
            if (!device.getDeviceid().equals(clientIdSplit.get(1))) {
                // 向客户端返回账号密码不正确消息，其实客户端有重连机制，可直接关闭
                MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(new MqttFixedHeader(
                                MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, true), null);
                channel.writeAndFlush(okResp);
                disConnect(channel, msg);
                return;
            }
/*            String clientId = msg.payload().clientIdentifier();
            if (initParamDTO.getClientIds().stream().filter(e -> e.equals(clientId)).collect(Collectors.toList()).size() == 0) {
                log.info("客户端id不匹配，通道关闭===>{}", clientId);
                // 向客户端返回账号密码不正确消息，其实客户端有重连机制，可直接关闭
//            MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(new MqttFixedHeader(
//                            MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, false, 0),
//                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, true), null);
//            channel.writeAndFlush(okResp);
                disConnect(channel, msg);
                return;
            }*/
        }

        //连接业务校验完成,Qos1类型，需要答复
        MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(new MqttFixedHeader(
                        MqttMessageType.CONNACK, false, AT_LEAST_ONCE, false, 0),
                new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, true), null);
        channel.writeAndFlush(okResp);

        addClient(channel, msg.toString());
    }

    public void pingReq(Channel channel, MqttMessage msg) {
        if (log.isDebugEnabled()) {
            log.debug("MQTT pingReq received.");
        }
        MqttMessage pingResp = new MqttMessage(new MqttFixedHeader(MqttMessageType.PINGRESP, false,
                AT_LEAST_ONCE, false, 0));
        channel.writeAndFlush(pingResp);
    }

    public void disConnect(Channel channel, MqttMessage msg) {
        if (channel.isActive()) {
            channel.close();
            if (log.isDebugEnabled()) {
                log.debug("MQTT channel '{}' was closed.", channel.id().asShortText());
            }
            removeClient(channel, msg.toString());
        }
    }

    public void puback(Channel channel, MqttPubAckMessage msg) {
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader = msg.variableHeader();
    }

    public void publish(Channel channel, MqttPublishMessage msg) {

        if (initParamDTO.getCheckQos() != msg.fixedHeader().qosLevel()) {
            log.info("qos类型不是{}，而是{}", initParamDTO.getCheckQos().value(), msg.fixedHeader().qosLevel());
            disConnect(channel, msg);
            return;
        }
        ByteBuf buf = msg.content().duplicate();
        byte[] tmp = new byte[buf.readableBytes()];
        buf.readBytes(tmp);
        String content = null;
        try {
            content = new String(tmp, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //校验传入的数据是否符合要求
        if (StringUtils.isBlank(content)) {
            log.error("MQTT接收到的数据包为空===》{}", content);
            puback(channel, msg, "MQTT接收到的数据包为空");
            return;
        }
        // 校验接收的数据是否是JSON格式
        if (initParamDTO.getAuthFormatToJson()) {
            if (!isJsonObject(content)) {
                log.error("MQTT接收到的数据包不为JSON格式===》{}", content);
                puback(channel, msg, "MQTT接收到的数据包不为JSON格式");
                return;
            }
        }
        log.info("MQTT读取到的客户端发送信息===>{}", content);
        String topic = msg.variableHeader().topicName();    //主题名称
        System.out.println(topic);
        log.info("MQTT发送到的客户端发送信息===>{}", content);
        List<String> topicSplit = StrSplitter.split(topic, '/', 0, true, true);
        String DeviceId = topicSplit.get(1);
        if (Objects.equals(DeviceId, myDeviceId)) {//校验主题ID和登录ID是否相同
            System.out.println(initParamDTO.getClientCache());
            System.out.println(initParamDTO.getClientCache().getAllList());
            MqttServer mqttServer = new MqttServer();
            for (ClientDTO clientDTO : initParamDTO.getClientCache().getAllList()) {
                try {
                    send(clientDTO.getChannel(), topic, initParamDTO.getCheckQos(), content);
                } catch (Exception e) {
                    log.error("主题:{}，推送用户{}失败", topic, clientDTO.getChannel().id());
                }
            }
//            List<String> topicSplit = StrSplitter.split(topic, '/', 0, true, true);
//            String DeviceId = topicSplit.get(1);
            System.out.println("topicSplit:" + topicSplit);
            Device device = deviceMapper.selectOne(new QueryWrapper<Device>().eq("deviceid", DeviceId));
            System.out.println(device);
            if (device == null) {
                log.error("topic不正确");
                return;
            }
            //		2.判断头是不是sys 尾部是不是post
            if ("sys".equals(topicSplit.get(0)) && "post".equals(topicSplit.get(2))) {
                //3.判断DeviceId和ProductKey是否对应
//            if (topicSplit.get(1).equals(DeviceId)) { //判断连接的设备ID是否一致
                if (topicSplit.get(1).equals(myDeviceId)) {
                    System.out.println("topic OK");
                    JSONObject jsonObject = JSON.parseObject(content);

                    //{\"paramdata\":[{\"humi\":9399,\"Geo\":\"223\"}]}
                    JSONObject msgObject = jsonObject.getObject("msg", JSONObject.class);
                    // [{\"humi\":9399,\"Geo\":\"223\"}]
                    JSONArray paramdata = msgObject.getJSONArray("paramdata");
                    //{\"humi\":9399,\"Geo\":\"223\"}
                    JSONObject paramdataObject = (JSONObject) paramdata.get(0);
                    //获取所有的Key
                    Set<String> keySet = paramdataObject.keySet();
                    System.out.println("test");
                    //实例化实时数据数组
                    Collection<Newdata> newDataCollection = new ArrayList();
                    //查询实时数据表下和myDeviceId相同的数据并删除
                    newdataMapper.delete(new QueryWrapper<Newdata>().eq("deviceid", myDeviceId));
                    //遍历所有的Key
                    for (String key : keySet) {
                        //获得key对应的值
                        String value = paramdataObject.getString(key);
                        //ParamsName赋值
                        System.out.println("publish key--" + key);
                        //ParamsValues赋值
                        System.out.println("publish value--" + value);


                        //实例化对象
                        Newdata jxNewData = new Newdata();
                        //DeviceId赋值
                        jxNewData.setDeviceid(DeviceId);
                        //ParamsName赋值
                        jxNewData.setParamsname(key);
                        //ParamsValues赋值
                        jxNewData.setParamsvalues(value);
                        newDataCollection.add(jxNewData);
                        jxNewData.setAccepttime((int) (System.currentTimeMillis() / 1000));
                        //添加到实时数据数组
                        newDataCollection.add(jxNewData);
                        newDataService.saveOrUpdateByMultiId(jxNewData);
                    }
                    //实例化历史数据数组
                    Collection<Historydata> historyDataCollection = new ArrayList();
                    //将实时数据插入历史数据
                    int num = 0;
                    for (Newdata jxNewData : newDataCollection) {
                        num++;
                        if (num % 2 != 0) {
                            //实例化 历史数据
                            Historydata jxHistoryData = new Historydata();
                            //复制实时数据 历史数据
                            BeanUtils.copyProperties(jxNewData, jxHistoryData);
                            //添加到历史数据数组
                            historyDataCollection.add(jxHistoryData);
                        }
                    }
                    //批量插入 历史数据
                    historydataService.saveBatch(historyDataCollection);
                }
            }
        }
/*        // 校验是否需要返回应答
        if (AT_LEAST_ONCE == initParamDTO.getCheckQos() || EXACTLY_ONCE == initParamDTO.getCheckQos()) {
            String resultMessage = null;
            // 如果需要加载业务逻辑
            if (initParamDTO.getDataService() != null) {
                String topic = msg.variableHeader().topicName();    //主题名称
                resultMessage = initParamDTO.getDataService().inputListening(channel, topic, content);
            }
            puback(channel, msg, resultMessage);
        }*/
    }

    public void subscribe(Channel channel, MqttSubscribeMessage msg) {
        MqttSubAckMessage subAckMessage = (MqttSubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.SUBACK, false, AT_LEAST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()),
                new MqttSubAckPayload(0));
        channel.writeAndFlush(subAckMessage);
    }

    public void unSubscribe(Channel channel, MqttUnsubscribeMessage msg) {
        MqttUnsubAckMessage unSubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBACK, false, AT_LEAST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()), null);
        channel.writeAndFlush(unSubAckMessage);
    }

    // 客户端远程关闭
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        log.error("MQTT客户端被强制关闭:{}:{}", ctx.channel().id().asShortText(), cause);
        if (ctx.channel().isActive()) {
            ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            removeClient(ctx.channel(), ctx.channel().toString());
        }
    }

    // 客户端新增
    private void addClient(Channel channel, String message) {
        if (initParamDTO.getClientCache() != null) {
            initParamDTO.getClientCache().add(channel, message);
        }
    }

    // 删除客户端
    private void removeClient(Channel channel, String message) {
        if (initParamDTO.getClientCache() != null) {
            initParamDTO.getClientCache().remove(channel, message);
        }
    }

    // 客户端订阅主题
    private void addTopic(Channel channel, String topic, String message) {
        if (initParamDTO.getITopicCache() != null) {
            initParamDTO.getITopicCache().add(channel, topic, message);
        }
    }

    // 客户端取消订阅主题
    private void removeTopic(Channel channel, String topic, String message) {
        if (initParamDTO.getITopicCache() != null) {
            initParamDTO.getITopicCache().remove(channel, topic, message);
        }
    }

    // 客户端QOS1消息类型( MqttQoS.AT_LEAST_ONCE = qos1)，需要服务器响应包
    private void puback(Channel channel, MqttPublishMessage msg, String payLoad) {
        MqttPubAckMessage sendMessage = (MqttPubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_LEAST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().packetId()),
                payLoad);
        channel.writeAndFlush(sendMessage);
    }

    public MqttHandler(InitParamDTO initParamDTO) {
        this.initParamDTO = initParamDTO;
    }

    /**
     * 判断字符串是否可以转化为json对象
     *
     * @param content
     * @return
     */
    public static boolean isJsonObject(String content) {
        if (StringUtils.isBlank(content))
            return false;
        try {
            JSONObject jsonStr = JSONObject.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 向客户端发布订阅主题消息
     *
     * @param channel:
     * @param topic:
     * @param qos:
     * @param sendMessage:
     * @return
     * @author liyang
     * @since 2022/3/13 23:16
     */
    public ChannelFuture send(Channel channel, String topic, MqttQoS qos, String sendMessage) throws InterruptedException {
        MqttRequest request = new MqttRequest((sendMessage.getBytes()));
        MqttPublishMessage pubMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBLISH,
                        request.isDup(),
                        qos,
                        request.isRetained(),
                        0),
                new MqttPublishVariableHeader(topic, 0),
                Unpooled.buffer().writeBytes(request.getPayload()));
        msgMap.put(pubMessage.variableHeader().messageId() + "", pubMessage.variableHeader().messageId() + "");
        System.out.println("发送数据");
        // 超过高水位，则采取同步模式
        if (channel.isWritable()) {
            return channel.writeAndFlush(pubMessage);
        }
        return channel.writeAndFlush(pubMessage).sync();
    }
}
