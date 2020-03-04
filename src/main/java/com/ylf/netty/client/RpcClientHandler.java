package com.ylf.netty.client;

import com.ylf.netty.entity.MyDataInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Callable;

/**
 * @author: leifeng.ye
 * @date: 2020-03-03
 * @desc:
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<MyDataInfo.MyData> implements Callable<Integer>{

    public ChannelHandlerContext context;

    public MyDataInfo.MyData param;

    public int result;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("远程server:"+ctx.channel().remoteAddress().toString().substring(1));
        context=ctx;
    }

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext channelHandlerContext, MyDataInfo.MyData myData) throws Exception {
        if(myData.getType()== MyDataInfo.MyData.Type.Res){
            this.result=myData.getRes().getRes();
            notify();
            channelHandlerContext.close();
        }
    }


    @Override
    public synchronized Integer call() throws InterruptedException {
        context.channel().writeAndFlush(param);
        System.out.println("[客户端]:发送"+param.getNum().getN1()+
                " "+param.getNum().getN2());
        wait();
        return this.result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       ctx.close();
    }

    public void setParam(MyDataInfo.MyData param) {
        this.param = param;
    }

}
