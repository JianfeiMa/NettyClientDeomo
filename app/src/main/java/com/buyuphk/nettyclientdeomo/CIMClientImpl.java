package com.buyuphk.nettyclientdeomo;

import android.content.Context;
import android.util.Log;

import com.buyuphk.nettyclientdeomo.handle.CIMClientHandle;
import com.buyuphk.nettyclientdeomo.init.CIMClientHandleInitializer;
import com.buyuphk.nettyclientdeomo.service.RouteRequest;
import com.buyuphk.nettyclientdeomo.service.impl.ClientInfo;
import com.buyuphk.nettyclientdeomo.service.impl.RouteRequestImpl;
import com.buyuphk.nettyclientdeomo.vo.req.LoginReqVO;
import com.buyuphk.nettyclientdeomo.vo.res.CIMServerResVO;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.protocol.CIMRequestProto;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class CIMClientImpl extends AbstractClient {
    private EventLoopGroup group = new NioEventLoopGroup(0, new DefaultThreadFactory("cim-work"));
    private long userId;
    private String userName;
    private RouteRequest routeRequest;
    private SocketChannel channel;
    private ClientInfo clientInfo;
    /**
     * 重试次数
     */
    private int errorCount;
    private Context context;

    public CIMClientImpl(Context context, long userId, String userName) {
        this.context = context;
        this.userId = userId;
        this.userName = userName;
        routeRequest = new RouteRequestImpl(userId, userName);
        clientInfo = new ClientInfo();
    }

    /**
     * 第一步执行：通过http请求验证用户id和用户名，返回即时通讯服务器的socket IP地址和端口号
     * @return
     */
    @Override
    public CIMServerResVO.ServerInfo userLogin() {
        LoginReqVO loginReqVO = new LoginReqVO(userId, userName);
        CIMServerResVO.ServerInfo serverInfo = null;
        try {
            serverInfo = routeRequest.getCIMServer(loginReqVO);
            clientInfo.saveServiceInfo(serverInfo.getIp() + ":" + + serverInfo.getCimServerPort())
                    .saveUserInfo(userId, userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverInfo;
    }

    /**
     * 第二步执行：与即时通讯服务器建立socket通道连接
     * @param serverInfo
     */
    @Override
    public void launchNetty(CIMServerResVO.ServerInfo serverInfo) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new CIMClientHandleInitializer(context));

        ChannelFuture future = null;
        System.out.println("cimServer.getIp->" + serverInfo.getIp() + ";cimServer.getCimServerPort->" + serverInfo.getCimServerPort());
        try {
            future = bootstrap.connect(serverInfo.getIp(), serverInfo.getCimServerPort()).sync();
        } catch (InterruptedException e) {
            errorCount++;

//            if (errorCount >= configuration.getErrorCount()) {
//                LOGGER.error("连接失败次数达到上限[{}]次", errorCount);
//                msgHandle.shutdown();
//            }
//            LOGGER.error("连接失败", e);
        }
        if (future.isSuccess()) {
            //echoService.echo("start cim client success!");
            //LOGGER.info("启动 cim client 成功");
            Log.d("debug", "启动 cim client 成功");
        }
        channel = (SocketChannel) future.channel();
    }

    /**
     * 第三步：向即时通讯服务器登记指定的用户id和用户名是在线状态，为后续其他用户推送消息过来
     */
    @Override
    public void loginCIMServer() {
        CIMRequestProto.CIMReqProtocol login = CIMRequestProto.CIMReqProtocol.newBuilder()
                .setRequestId(userId)
                .setReqMsg(userName)
                .setType(Constants.CommandType.LOGIN)
                .build();
        ChannelFuture future = channel.writeAndFlush(login);
        future.addListener((ChannelFutureListener) channelFuture ->
                Log.d("debug", "")
        );
    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
    }


}
