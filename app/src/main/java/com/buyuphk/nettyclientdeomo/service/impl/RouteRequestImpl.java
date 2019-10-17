package com.buyuphk.nettyclientdeomo.service.impl;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.buyuphk.nettyclientdeomo.config.AppConfiguration;
import com.buyuphk.nettyclientdeomo.enums.StatusEnum;
import com.buyuphk.nettyclientdeomo.service.RouteRequest;
import com.buyuphk.nettyclientdeomo.vo.req.GroupReqVO;
import com.buyuphk.nettyclientdeomo.vo.req.LoginReqVO;
import com.buyuphk.nettyclientdeomo.vo.req.P2PReqVO;
import com.buyuphk.nettyclientdeomo.vo.res.CIMServerResVO;
import com.buyuphk.nettyclientdeomo.vo.res.OfflineUserResVO;
import com.buyuphk.nettyclientdeomo.vo.res.OnlineUsersResVO;
import com.crossoverjie.cim.common.res.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Function:
 * 发送消息全部是通过http(超文本协议发送)，只有接收消失才通过socket传输
 * @author crossoverJie
 *         Date: 2018/12/22 22:27
 * @since JDK 1.8
 */
public class RouteRequestImpl implements RouteRequest {


    private OkHttpClient okHttpClient ;

    private MediaType mediaType = MediaType.parse("application/json");

    private String groupRouteRequestUrl;

    private String p2pRouteRequestUrl;

    private String serverRouteRegisterUrl;

    private String serverRouteLoginUrl;

    private String onlineUserUrl;

    private AppConfiguration appConfiguration ;

    public RouteRequestImpl(long userId, String userName) {
        okHttpClient = new OkHttpClient();
        groupRouteRequestUrl = "http://192.168.1.33:8083/groupRoute";
        p2pRouteRequestUrl = "http://192.168.1.33:8083/p2pRoute";
        serverRouteRegisterUrl = "http://192.168.1.33:8083/registerAccount";
        serverRouteLoginUrl = "http://192.168.1.33:8083/login";
        onlineUserUrl = "http://192.168.1.33:8083/onlineUser";
        appConfiguration = new AppConfiguration();
        appConfiguration.setUserId(userId);
        appConfiguration.setUserName(userName);
        appConfiguration.setClearRouteUrl("http://192.168.1.33:8083/offLine");
        appConfiguration.setHeartBeatTime(60L);
        appConfiguration.setErrorCount(3);
    }

    @Override
    public void sendGroupMsg(GroupReqVO groupReqVO) throws Exception {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg",groupReqVO.getMsg());
        jsonObject.put("userId",groupReqVO.getUserId());
        RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());

        Request request = new Request.Builder()
                .url(groupRouteRequestUrl)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute() ;
        try {
            if (!response.isSuccessful()){
                throw new IOException("Unexpected code " + response);
            }
        }finally {
            response.body().close();
        }
    }

    @Override
    public void sendP2PMsg(P2PReqVO p2PReqVO) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg",p2PReqVO.getMsg());
        jsonObject.put("userId",p2PReqVO.getUserId());
        jsonObject.put("receiveUserId",p2PReqVO.getReceiveUserId());
        RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());

        Request request = new Request.Builder()
                .url(p2pRouteRequestUrl)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute() ;
        if (!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
        }

        ResponseBody body = response.body();
        try {
            String json = body.string() ;
            BaseResponse baseResponse = JSON.parseObject(json, BaseResponse.class);

            //选择的账号不存在
            if (baseResponse.getCode().equals(StatusEnum.OFF_LINE.getCode())){
                //LOGGER.error(p2PReqVO.getReceiveUserId() + ":" + StatusEnum.OFF_LINE.getMessage());
                Log.d("debug", p2PReqVO.getReceiveUserId() + ":" + StatusEnum.OFF_LINE.getMessage());
            }

        }finally {
            body.close();
        }
    }

    @Override
    public CIMServerResVO.ServerInfo getCIMServer(LoginReqVO loginReqVO) throws Exception {
        System.out.println("向服务器注册一个账户");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId",loginReqVO.getUserId());
        jsonObject.put("userName",loginReqVO.getUserName());
        RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());

        Request request = new Request.Builder()
                .url(serverRouteLoginUrl)
                .post(requestBody)
                .build();
//        Request request = new Request.Builder()
//                .url(serverRouteRegisterUrl)
//                .post(requestBody)
//                .build();

        Response response = okHttpClient.newCall(request).execute() ;
        if (!response.isSuccessful()){
            throw new IOException("没有成功Unexpected code " + response);
        }
        CIMServerResVO cimServerResVO ;
        ResponseBody body = response.body();
        try {
            String json = body.string();
            System.out.println("注册一个账户返回的结果->" + json);
            System.out.println("RouteRequestImpl.getCiMServer->" + json);
            cimServerResVO = JSON.parseObject(json, CIMServerResVO.class);

            //重复失败
            if (!cimServerResVO.getCode().equals(StatusEnum.SUCCESS.getCode())){
                //echoService.echo(cimServerResVO.getMessage());
                Log.d("debug", cimServerResVO.getMessage());
                //System.exit(-1);
            }

        }finally {
            body.close();
        }



        return cimServerResVO.getDataBody();
    }

    @Override
    public List<OnlineUsersResVO.DataBodyBean> onlineUsers() throws Exception{

        JSONObject jsonObject = new JSONObject();
        RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());

        Request request = new Request.Builder()
                .url(onlineUserUrl)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute() ;
        if (!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
        }


        ResponseBody body = response.body();
        OnlineUsersResVO onlineUsersResVO ;
        try {
            String json = body.string() ;
            Log.d("debug", "返回所有在线用户->" + json);
            onlineUsersResVO = JSON.parseObject(json, OnlineUsersResVO.class);

        }finally {
            body.close();
        }

        return onlineUsersResVO.getDataBody();
    }

    @Override
    public OfflineUserResVO offLine() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", appConfiguration.getUserId());
            jsonObject.put("msg", "offLine");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());

        Request request = new Request.Builder()
                .url(appConfiguration.getClearRouteUrl())
                .post(requestBody)
                .build();

        Response response = null;
        OfflineUserResVO offlineUserResVO = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            //LOGGER.error("exception",e);
            Log.e("debug", e.getMessage());
        } finally {
            try {
                String sResponse = response.body().string();
                offlineUserResVO = JSON.parseObject(sResponse, OfflineUserResVO.class);
                //Log.d("debug", "下线请求返回结果->" + response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            response.body().close();
        }
        return offlineUserResVO;
    }
}
