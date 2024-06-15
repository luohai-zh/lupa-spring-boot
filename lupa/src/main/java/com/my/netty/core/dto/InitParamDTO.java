package com.my.netty.core.dto;

import com.my.netty.core.cache.IClientCache;
import com.my.netty.core.cache.ITopicCache;
import com.my.netty.core.service.IDataService;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * 服务初始参数
 * @author liyang
 * @date 2021/3/9 11:47
 */
@Data
public class InitParamDTO {
    // 端口号
    private Integer port = 8888;
    // 主线程
    private Integer boosTread = 1;
    // 子线程
    private Integer workTread = 1;
    // 账号
    private String name="admin";
    // 密码
    private String password="123456";
    // 客户端id
    private List<String> clientIds= Arrays.asList(new String[]{"c1","c2","c3"});
    // qos类型
    private MqttQoS checkQos = MqttQoS.AT_MOST_ONCE;
    // 登录权限开关（默认关闭）
    private Boolean authLoginOnOff = true;
    // 客户端连接id验证开关（默认关闭）
    private Boolean authClientIdOnOff = true;
    // 校验数据是否是JSON格式
    private Boolean authFormatToJson = true;
    // 业务逻辑
    private IDataService dataService;
    // 客户端
    private IClientCache clientCache;
    // 主题
    private ITopicCache iTopicCache;

    public InitParamDTO() {
    }
    public static InitParamDTO base(Integer port){
        return base(port,1,1);
    }

    public static InitParamDTO base(Integer port, IDataService dataService){
        return base(port,1,1,dataService,null);
    }

    public static InitParamDTO base(Integer port, Integer boosTread, Integer workTread){
        return base(port,boosTread,workTread,null,null);
    }

    public static InitParamDTO base(Integer port, Integer boosTread, Integer workTread, IDataService dataService, IClientCache clientCache){
        InitParamDTO initParamDTO = new InitParamDTO();
        initParamDTO.setPort(port);
        initParamDTO.setWorkTread(workTread);
        initParamDTO.setBoosTread(boosTread);
        initParamDTO.setDataService(dataService);
        initParamDTO.setClientCache(clientCache);
        return initParamDTO;
    }
}
