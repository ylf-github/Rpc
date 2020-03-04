package com.ylf;

import com.ylf.netty.client.RpcClient;
import com.ylf.serviceAPI.MaxApi;

/**
 * @author: leifeng.ye
 * @date: 2020-03-03
 * @desc:
 */
public class StartServerMaxClient {
    public static void main(String[] args) throws InterruptedException {
        RpcClient client=new RpcClient();
        MaxApi math=(MaxApi)client.getBead(MaxApi.class);
        int result=math.getMax(16,22);
        System.out.println("计算结果为:"+result);
    }
}
