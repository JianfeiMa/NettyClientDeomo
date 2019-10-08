package com.buyuphk.nettyclientdeomo.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/05/21 15:56
 * @since JDK 1.8
 */
public class SendMsgReqVO extends BaseRequest {

    private String msg ;

    private Long userId ;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
