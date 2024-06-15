package com.my.netty.core.server;

import com.my.netty.core.dto.InitParamDTO;
import lombok.Data;
import lombok.Getter;

/**
 * @author liyang
 * @date 2021/3/25 16:10
 */
public abstract class AbsServer implements IServer {
    // 服务器参数
    protected InitParamDTO initParamDTO;
}
