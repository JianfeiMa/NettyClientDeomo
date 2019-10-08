package com.buyuphk.nettyclientdeomo.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;

/**
 * Function: 群发请求
 *
 * @author crossoverJie
 *         Date: 2018/05/21 15:56
 * @since JDK 1.8
 */
public class GroupReqVO extends BaseRequest {

    private Long userId ;


    private String msg ;

    public GroupReqVO(Long userId, String msg) {
        this.userId = userId;
        this.msg = msg;
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
