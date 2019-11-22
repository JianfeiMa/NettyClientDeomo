package com.buyuphk.nettyclientdeomo.service.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.buyuphk.nettyclientdeomo.AbstractClient;
import com.buyuphk.nettyclientdeomo.CIMClientImpl;
import com.crossoverjie.cim.common.kit.HeartBeatHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-20 17:16
 * @since JDK 1.8
 */
public class ClientHeartBeatHandlerImpl implements HeartBeatHandler {

    private AbstractClient abstractClient;

    public ClientHeartBeatHandlerImpl(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userId = sharedPreferences.getString("userId", "");
        if (userId == null || userId.equals("")) {
            userId = "0";
            Log.d(getClass().getSimpleName(), "重连用户ID为空");
        }
        String userName = sharedPreferences.getString("userName", "");
        this.abstractClient = new CIMClientImpl(context, Long.valueOf(userId), userName);
    }

    @Override
    public void process(ChannelHandlerContext ctx) throws Exception {
        //重连
        abstractClient.reconnect();
    }
}
