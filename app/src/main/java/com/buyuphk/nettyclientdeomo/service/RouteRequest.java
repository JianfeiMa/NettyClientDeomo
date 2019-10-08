package com.buyuphk.nettyclientdeomo.service;

import com.buyuphk.nettyclientdeomo.vo.req.GroupReqVO;
import com.buyuphk.nettyclientdeomo.vo.req.LoginReqVO;
import com.buyuphk.nettyclientdeomo.vo.req.P2PReqVO;
import com.buyuphk.nettyclientdeomo.vo.res.CIMServerResVO;
import com.buyuphk.nettyclientdeomo.vo.res.OnlineUsersResVO;

import java.util.List;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/22 22:26
 * @since JDK 1.8
 */
public interface RouteRequest {

    /**
     * 群发消息
     * @param groupReqVO 消息
     * @throws Exception
     */
    void sendGroupMsg(GroupReqVO groupReqVO) throws Exception;


    /**
     * 私聊
     * @param p2PReqVO
     * @throws Exception
     */
    void sendP2PMsg(P2PReqVO p2PReqVO)throws Exception;

    /**
     * 获取服务器
     * @return 服务ip+port
     * @param loginReqVO
     * @throws Exception
     */
    CIMServerResVO.ServerInfo getCIMServer(LoginReqVO loginReqVO) throws Exception;

    /**
     * 获取所有在线用户
     * @return
     * @throws Exception
     */
    List<OnlineUsersResVO.DataBodyBean> onlineUsers()throws Exception ;


    void offLine() ;

}
