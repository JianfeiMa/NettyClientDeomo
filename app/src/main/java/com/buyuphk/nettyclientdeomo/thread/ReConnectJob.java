package com.buyuphk.nettyclientdeomo.thread;

import android.content.Context;

import com.buyuphk.nettyclientdeomo.service.impl.ClientHeartBeatHandlerImpl;
import com.crossoverjie.cim.common.kit.HeartBeatHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-20 21:35
 * @since JDK 1.8
 */
public class ReConnectJob implements Runnable {

    private ChannelHandlerContext context ;

    private HeartBeatHandler heartBeatHandler ;

    public ReConnectJob(ChannelHandlerContext context, Context c) {
        this.context = context;
        this.heartBeatHandler = new ClientHeartBeatHandlerImpl(c);
    }

    @Override
    public void run() {
        try {
            heartBeatHandler.process(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
