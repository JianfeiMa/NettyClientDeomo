package com.buyuphk.nettyclientdeomo.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;

/**
 * Function: Google Protocol 编解码发送
 *
 * @author crossoverJie
 *         Date: 2018/05/21 15:56
 * @since JDK 1.8
 */
public class GoogleProtocolVO extends BaseRequest {
    private Integer requestId ;

    private String msg ;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "GoogleProtocolVO{" +
                "requestId=" + requestId +
                ", msg='" + msg + '\'' +
                "} " + super.toString();
    }
}
