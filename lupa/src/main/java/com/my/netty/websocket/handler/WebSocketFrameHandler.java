package com.my.netty.websocket.handler;

import com.my.netty.core.dto.InitParamDTO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;


/**
 * @author liyang
 * @date 2021/3/25 16:21
 */
@Slf4j
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private InitParamDTO initParamDTO;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("收到消息: " + msg.text());
        String s = initParamDTO.getDataService().inputListening(ctx.channel(), null, msg.text());
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("新用户加入: " + ctx.channel().id().asLongText());
        initParamDTO.getClientCache().add(ctx.channel(),null);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("用户退出: " + ctx.channel().id().asLongText());
        initParamDTO.getClientCache().remove(ctx.channel(),null);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发生");
        if (ctx.channel().isActive()) {
            initParamDTO.getClientCache().remove(ctx.channel(),"异常发生");
            ctx.close();
        }
    }

    public WebSocketFrameHandler(InitParamDTO initParamDTO) {
        this.initParamDTO = initParamDTO;
    }

    public WebSocketFrameHandler() {
    }
}
