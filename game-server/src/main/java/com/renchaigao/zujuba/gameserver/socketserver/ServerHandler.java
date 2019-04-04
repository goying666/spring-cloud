package com.renchaigao.zujuba.gameserver.socketserver;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.SocketBean.NormalUse;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.PlatformDependent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

@Getter
@Setter
@Component
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    //保留所有与服务器建立连接的channel对象，这边的GlobalEventExecutor在写博客的时候解释一下，看其doc
//    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private SocketUsers socketUsers = SocketUsers.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) {
        NormalUse normalUse = JSONObject.parseObject((String) o, NormalUse.class);
        if (normalUse.getUserId() != null) {
            SocketUsers.put(normalUse.getUserId(), channelHandlerContext.channel());
            switch (normalUse.getState()) {

            }
        }
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
//        socketUsers.put(ctx.channel());
//        channelGroup.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channelActive>>>>>>>>");
    }
}