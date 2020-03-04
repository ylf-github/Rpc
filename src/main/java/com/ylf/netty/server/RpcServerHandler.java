package com.ylf.netty.server;

import com.ylf.netty.entity.MyDataInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: leifeng.ye
 * @date: 2020-03-03
 * @desc:
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<MyDataInfo.MyData> {


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MyDataInfo.MyData data) throws Exception {

        if(data.getType()== MyDataInfo.MyData.Type.Num){
            MyDataInfo.Nums numbers=data.getNum();
            int result=new MathService().getMax(numbers.getN1(),numbers.getN2());
            MyDataInfo.MyData res = MyDataInfo.MyData.newBuilder().setType(MyDataInfo.MyData.Type.Res)
                    .setRes(MyDataInfo.Res.newBuilder().setRes(result).build()).build();
            channelHandlerContext.channel().writeAndFlush(res);
        }
    }
}
