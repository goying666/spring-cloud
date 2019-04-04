package com.renchaigao.zujuba.gameserver.socketserver;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class NettyServer {
    private static final Logger log = Logger.getLogger(NettyServer.class);

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Channel channel;
    //boss事件轮询线程组
    private EventLoopGroup boss = new NioEventLoopGroup();
    //worker事件轮询线程组
    private EventLoopGroup worker = new NioEventLoopGroup();

    @Autowired
    ServerChannelInitializer serverChannelInitializer;

    private Integer port =7802;

    /**
     * 开启Netty服务
     *
     * @return
     */
    public ChannelFuture start() {
        //启动类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)//设置参数，组配置
                .option(ChannelOption.SO_BACKLOG, 128)//socket参数，当服务器请求处理程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。
                .channel(NioServerSocketChannel.class)///构造channel通道工厂//bossGroup的通道，只是负责连接
                .childHandler(serverChannelInitializer);//设置通道处理者ChannelHandler////workerGroup的处理器
        //Future：异步操作的结果
        ChannelFuture channelFuture = serverBootstrap.bind(port);//绑定端口
        ChannelFuture channelFuture1 = channelFuture.syncUninterruptibly();//接收连接
        channel = channelFuture1.channel();//获取通道
        if (channelFuture1 != null && channelFuture1.isSuccess()) {
            log.info("Netty server 服务启动成功，端口port = {}" + port);
        } else {
            log.info("Netty server start fail");
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        return channelFuture1;
    }

//    /**
//     * 启动服务
//     */
//    public ChannelFuture run (InetSocketAddress address) {
//
//        ChannelFuture f = null;
//        try {
//            ServerBootstrap b = new ServerBootstrap();
//            b.group(bossGroup, workerGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .childHandler(new ServerChannelInitializer())
//                    .option(ChannelOption.SO_BACKLOG, 128)
//                    .childOption(ChannelOption.SO_KEEPALIVE, true);
//
//            f = b.bind(address).syncUninterruptibly();
//            channel = f.channel();
//        } catch (Exception e) {
//            log.error("Netty start error:", e);
//        } finally {
//            if (f != null && f.isSuccess()) {
//                log.info("Netty server listening " + address.getHostName() + " on port " + address.getPort() + " and ready for connections...");
//            } else {
//                log.error("Netty server start up Error!");
//            }
//        }
//
//        return f;
//    }

    public void destroy() {
        log.info("Shutdown Netty Server...");
        if(channel != null) { channel.close();}
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        log.info("Shutdown Netty Server Success!");
    }
}
