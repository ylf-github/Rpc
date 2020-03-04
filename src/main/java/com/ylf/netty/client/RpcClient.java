package com.ylf.netty.client;

import com.ylf.netty.entity.MyDataInfo;
import com.ylf.zk.ZkClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.apache.zookeeper.KeeperException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: leifeng.ye
 * @date: 2020-03-03
 * @desc:
 */
public class RpcClient implements Callable {

    //是用cpu默认核数的线程池
    private static ExecutorService executor=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static RpcClientHandler handler;


    public  Object getBead(Class serviceClass){
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{serviceClass},
                (proxy,method,args)->{
                      if(handler==null){
                        executor.submit(this);
                      }
                      //等待线程池提交的任务完成
                    synchronized (this){
                       wait();
                    }
                    MyDataInfo.MyData data = MyDataInfo.MyData.newBuilder()
                            .setType(MyDataInfo.MyData.Type.Num)
                            .setNum(MyDataInfo.Nums.newBuilder()
                                    .setN1((Integer) args[0]).setN2((Integer) args[1])
                                    .build()).build();
                    handler.setParam(data);
                      return executor.submit(handler).get();
                });
    }

    public  void startClientServerMax() throws KeeperException, InterruptedException {
        handler=new RpcClientHandler();
        NioEventLoopGroup workGroup=new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();

        try{
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            pipeline.addLast(new ProtobufDecoder(MyDataInfo.MyData.getDefaultInstance()));
                            pipeline.addLast(handler);
                            pipeline.addLast(new ProtobufEncoder());
                        }
                    });
            ArrayList<String> list= (ArrayList<String>) new ZkClient().getAvailableServerMax();
            if(list.size()==0){
                System.out.println("暂无可用的服务");
                return;
            }
            else {
                do{

                    //这里采用随机的负载策略
                    Random random=new Random();
                    int index=random.nextInt(list.size());
                    String hostName=list.get(index%list.size());
                    String host=hostName.split(":")[0];
                    int port=Integer.valueOf(hostName.split(":")[1]);
                    try {
                        ChannelFuture future = bootstrap.connect(host, port).sync();
                        System.out.println("客户端启动成功...");
                        synchronized (this) {
                            notify();
                        }
                        future.channel().closeFuture().sync();
                        break;
                    } catch (InterruptedException e) {

                    }
                }while (list.size()!=0);
                if(list.size()==0){
                    System.out.println("暂无可用的服务");
                    return;
                }
            }
        }finally {
            executor.shutdown();
            workGroup.shutdownGracefully();
        }
    }


    @Override
    public  Object call() throws Exception {
        try {
                startClientServerMax();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
