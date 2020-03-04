package com.ylf.zk;
import io.netty.util.CharsetUtil;
import org.apache.zookeeper.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: leifeng.ye
 * @date: 2020-03-02
 * @desc:
 */
public class ZkClient {

    private String zkConntion="120.27.246.207";
    private Integer sessionTimeout=20000;
    private ZooKeeper client;
    private ArrayList<String> list=new ArrayList<>();

    {
        try {
            client=new ZooKeeper(zkConntion, sessionTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean registerServerMax(String hostName) throws InterruptedException, KeeperException {
        boolean f=false;
        String path = null;
        path = client.create("/service/server-max/", hostName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        if(path!=null){
            f=true;
        }
        return f;
    }

    public List getAvailableServerMax() throws KeeperException, InterruptedException {
        ArrayList<String> l=new ArrayList<>();
        l= (ArrayList<String>) client.getChildren("/service/server-max", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try {
                    getAvailableServerMax();
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        list.clear();
        for(String path:l){
            byte[] data = client.getData("/service/server-max/" + path, false, null);
            list.add(new String(data,CharsetUtil.UTF_8));
        }
        return list;
    }
}
