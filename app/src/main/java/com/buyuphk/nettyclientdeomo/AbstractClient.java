package com.buyuphk.nettyclientdeomo;

import android.util.Log;

import com.buyuphk.nettyclientdeomo.vo.res.CIMServerResVO;

/**
 * 模板方法模式
 */
public abstract class AbstractClient {

    public void start() {
        CIMServerResVO.ServerInfo serverInfo = userLogin();
        if (serverInfo == null) {
            Log.d("debug", "登录失败");
            return;
        }
        launchNetty(serverInfo);
        loginCIMServer();
    }

    public abstract CIMServerResVO.ServerInfo userLogin();

    public abstract void launchNetty(CIMServerResVO.ServerInfo serverInfo);

    public abstract void loginCIMServer();
}
