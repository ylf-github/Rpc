package com.ylf.netty.server;

import com.ylf.netty.entity.MyDataInfo;
import com.ylf.zk.ZkClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.apache.zookeeper.KeeperException;


/**
 * @author: leifeng.ye
 * @date: 2020-03-03
 * @desc:
 */
public class RpcServer {



    public void startServerMax(String host,int port) throws InterruptedException, KeeperException {
        NioEventLoopGroup bossGroup=new NioEventLoopGroup();
        NioEventLoopGroup workGroup=new NioEventLoopGroup();
        ServerBootstrap bootstrap=new ServerBootstrap();

        try{
            bootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            pipeline.addLast(new ProtobufDecoder(MyDataInfo.MyData.getDefaultInstance()));
                            pipeline.addLast(new RpcServerHandler());
                            pipeline.addLast(new ProtobufEncoder());

                        }
                    });
            ChannelFuture future = bootstrap.bind(host, port).sync();

            String hostName=future.channel().localAddress().toString().substring(1);
            boolean f = new ZkClient().registerServerMax(hostName);
            if(f){
                System.out.println("服务注册成功...");
            }
            future.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
