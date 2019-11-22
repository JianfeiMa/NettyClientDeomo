package com.buyuphk.nettyclientdeomo.handle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.buyuphk.nettyclientdeomo.thread.ReConnectJob;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.protocol.CIMRequestProto;
import com.crossoverjie.cim.common.protocol.CIMResponseProto;
import com.crossoverjie.cim.common.util.NettyAttrUtil;
import com.vdurmont.emoji.EmojiParser;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 16/02/2018 18:09
 * @since JDK 1.8
 */
@ChannelHandler.Sharable
public class CIMClientHandle extends SimpleChannelInboundHandler<CIMResponseProto.CIMResProtocol> {

    private MsgHandleCaller caller ;

    private ThreadPoolExecutor threadPoolExecutor ;

    private ScheduledExecutorService scheduledExecutorService ;
    private Context context;

    public CIMClientHandle(Context context) {
        this.context = context;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Log.d("debug", "********CIMClientHandle执行了userEventTriggered方法********");
        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt ;
            //LOGGER.info("定时检测服务端是否存活");
            if (idleStateEvent.state() == IdleState.WRITER_IDLE){
                Log.d("debug", "writer_idle");
                Intent aliveIntent = new Intent("alive");
                context.sendBroadcast(aliveIntent);
//                CIMRequestProto.CIMReqProtocol heartBeat = SpringBeanFactory.getBean("heartBeat", CIMRequestProto.CIMReqProtocol.class);
//                ctx.writeAndFlush(heartBeat).addListeners((ChannelFutureListener) future -> {
//                    if (!future.isSuccess()) {
//                        //LOGGER.error("IO error,close Channel");
//                        future.channel().close();
//                    }
//                }) ;
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.d("debug", "********CIMClientHandle执行了channelActive方法********");
        //客户端和服务端建立连接时调用
        Log.d("debug", "cim server connect success!");
        saveConnectedStatus(true);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.d("debug", "********CIMClientHandle执行了channelInactive方法********");
        Log.d("debug", "客户端断开了，重新连接！");
        saveConnectedStatus(false);
        Intent intent = new Intent("channelInactive");
        context.sendBroadcast(intent);
//        if (shutDownMsg == null){
//            shutDownMsg = SpringBeanFactory.getBean(ShutDownMsg.class) ;
//        }
//        //用户主动退出，不执行重连逻辑
//        if (shutDownMsg.checkStatus()){
//            return;
//        }
        if (scheduledExecutorService == null){
            scheduledExecutorService = Executors.newScheduledThreadPool(10);
        }
        // TODO: 2019-01-22 后期可以改为不用定时任务，连上后就关闭任务 节省性能。
        scheduledExecutorService.scheduleAtFixedRate(new ReConnectJob(ctx, context),0,10, TimeUnit.SECONDS);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CIMResponseProto.CIMResProtocol msg) throws Exception {
        Log.d("debug", "********CIMClientHandle执行了channelRead0方法********");
//        if (echoService == null){
//            echoService = SpringBeanFactory.getBean(EchoServiceImpl.class) ;
//        }
        //心跳更新时间
        if (msg.getType() == Constants.CommandType.PING){
            //LOGGER.info("收到服务端心跳！！！");
            NettyAttrUtil.updateReaderTime(ctx.channel(),System.currentTimeMillis());
        }
        if (msg.getType() != Constants.CommandType.PING) {
            //回调消息
            callBackMsg(msg.getResMsg());
            //将消息中的 emoji 表情格式化为 Unicode 编码以便在终端可以显示
            String response = EmojiParser.parseToUnicode(msg.getResMsg());
            //echoService.echo(response);
        }
    }

    /**
     * 回调消息
     * @param msg
     */
    private void callBackMsg(String msg) {
        Log.d("debug", "接收到新的消息：" + msg);
//        threadPoolExecutor = SpringBeanFactory.getBean("callBackThreadPool",ThreadPoolExecutor.class) ;
//        threadPoolExecutor.execute(() -> {
//            caller = SpringBeanFactory.getBean(MsgHandleCaller.class) ;
//            caller.getMsgHandleListener().handle(msg);
//        });
        Intent intent = new Intent("netty_socket");
        intent.putExtra("msg", msg);
        context.sendBroadcast(intent);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Log.d("debug", "********CIMClientHandle执行了exceptionCaught方法********");
        //异常时断开连接
        cause.printStackTrace();
        ctx.close();
    }

    private void saveConnectedStatus(boolean connectedStatus) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isDisconnected", connectedStatus);
        editor.apply();
    }
}
