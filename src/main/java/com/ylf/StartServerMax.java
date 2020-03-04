package com.ylf;

import com.ylf.netty.server.RpcServer;
import org.apache.zookeeper.KeeperException;

/**
 * @author: leifeng.ye
 * @date: 2020-03-03
 * @desc:
 */
public class StartServerMax {
    public static void main(String[] args) throws KeeperException, InterruptedException {
        String host="127.0.0.1";
        int port=8088;
        RpcServer server = new RpcServer();
        server.startServerMax(host,port);
    }
}
