package com.buyuphk.nettyclientdeomo.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;

/**
 * Function: 单聊请求
 *
 * @author crossoverJie
 *         Date: 2018/05/21 15:56
 * @since JDK 1.8
 */
public class P2PReqVO extends BaseRequest {

    private Long userId ;


    private Long receiveUserId ;




    private String msg ;

    public P2PReqVO() {
    }

    public P2PReqVO(Long userId, Long receiveUserId, String msg) {
        this.userId = userId;
        this.receiveUserId = receiveUserId;
        this.msg = msg;
    }

    public Long getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(Long receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "GroupReqVO{" +
                "userId=" + userId +
                ", msg='" + msg + '\'' +
                "} " + super.toString();
    }
}
