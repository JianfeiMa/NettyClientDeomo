package com.buyuphk.nettyclientdeomo.service.impl;

import android.util.Log;

import com.buyuphk.nettyclientdeomo.CIMClientImpl;
import com.buyuphk.nettyclientdeomo.config.AppConfiguration;
import com.buyuphk.nettyclientdeomo.service.MsgHandle;
import com.buyuphk.nettyclientdeomo.service.RouteRequest;
import com.buyuphk.nettyclientdeomo.vo.req.GroupReqVO;
import com.buyuphk.nettyclientdeomo.vo.req.P2PReqVO;
import com.crossoverjie.cim.common.util.StringUtil;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/26 11:15
 * @since JDK 1.8
 */
public class MsgHandler implements MsgHandle {
    private RouteRequest routeRequest;

    private AppConfiguration configuration;

    //private ThreadPoolExecutor executor;

    private CIMClientImpl cimClient;

    //private MsgLogger msgLogger;

    private ClientInfo clientInfo;

    //private InnerCommandContext innerCommandContext ;

    private boolean aiModel = false;

    public MsgHandler(String userId, String userName) {
        configuration = new AppConfiguration();
        configuration.setUserId(Long.valueOf(userId));
        configuration.setUserName(userName);
        configuration.setClearRouteUrl("http://192.168.1.33:8083/offLine");
        configuration.setHeartBeatTime(60L);
        configuration.setErrorCount(3);
        //executor = new ThreadPoolExecutor()
        //cimClient = new CIMClientImpl();
        clientInfo = new ClientInfo();
        routeRequest = new RouteRequestImpl(Long.valueOf(userId), userName);
    }

    @Override
    public void sendMsg(String msg) {
        if (aiModel) {
            aiChat(msg);
        } else {
            normalChat(msg);
        }
    }

    /**
     * 正常聊天
     *
     * @param msg
     */
    private void normalChat(String msg) {
        String[] totalMsg = msg.split(";;");
        if (totalMsg.length > 1) {
            //私聊
            System.out.println("私聊->" + totalMsg[0] + ";发送内容->" + totalMsg[1]);
            P2PReqVO p2PReqVO = new P2PReqVO();
            p2PReqVO.setUserId(configuration.getUserId());
            p2PReqVO.setReceiveUserId(Long.parseLong(totalMsg[0]));
            p2PReqVO.setMsg(totalMsg[1]);
            try {
                p2pChat(p2PReqVO);
            } catch (Exception e) {
                System.out.println("私聊异常");
                //LOGGER.error("Exception", e);
                Log.e("debug", e.getMessage());
            }

        } else {
            //群聊
            System.out.println("群聊->发送内容：" + msg);
            GroupReqVO groupReqVO = new GroupReqVO(configuration.getUserId(), msg);
            try {
                groupChat(groupReqVO);
            } catch (Exception e) {
                System.out.println("群聊异常");
                //LOGGER.error("Exception", e);
                Log.e("debug", e.getMessage());
            }
        }
    }

    /**
     * AI model
     *
     * @param msg
     */
    private void aiChat(String msg) {
        msg = msg.replace("吗", "");
        msg = msg.replace("嘛", "");
        msg = msg.replace("?", "!");
        msg = msg.replace("？", "!");
        msg = msg.replace("你", "我");
        System.out.println("AI:\033[31;4m" + msg + "\033[0m");
    }

    @Override
    public void groupChat(GroupReqVO groupReqVO) throws Exception {
        routeRequest.sendGroupMsg(groupReqVO);
    }

    @Override
    public void p2pChat(P2PReqVO p2PReqVO) throws Exception {

        routeRequest.sendP2PMsg(p2PReqVO);

    }

    @Override
    public boolean checkMsg(String msg) {
        if (StringUtil.isEmpty(msg)) {
            //LOGGER.warn("不能发送空消息！");
            Log.w("debug", "不能发送空消息！");
            return true;
        }
        return false;
    }

    @Override
    public boolean innerCommand(String msg) {

        if (msg.startsWith(":")) {

            //InnerCommand instance = innerCommandContext.getInstance(msg);
            //instance.process(msg) ;

            return true;

        } else {
            return false;
        }


    }

    /**
     * 关闭系统
     */
    @Override
    public void shutdown() {
        //LOGGER.info("系统关闭中。。。。");
        routeRequest.offLine();
        //msgLogger.stop();
        //executor.shutdown();
        //try {
//            while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
//                LOGGER.info("线程池关闭中。。。。");
//                Log.d("debug", "线程池关闭中。。。。");
//            }
            cimClient.close();
        //} catch (InterruptedException e) {
            //LOGGER.error("InterruptedException", e);
        //    Log.e("debug", e.getMessage());
        //}
        //System.exit(0);
    }

    @Override
    public void openAIModel() {
        aiModel = true;
    }

    @Override
    public void closeAIModel() {
        aiModel = false ;
    }

}
