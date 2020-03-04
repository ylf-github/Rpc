package com.ylf.netty.server;

import com.ylf.serviceAPI.MaxApi;

/**
 * @author: leifeng.ye
 * @date: 2020-03-03
 * @desc:
 */
public class MathService implements MaxApi {
    @Override
    public int getMax(int n1, int n2) {
        return java.lang.Math.max(n1,n2);
    }
}
