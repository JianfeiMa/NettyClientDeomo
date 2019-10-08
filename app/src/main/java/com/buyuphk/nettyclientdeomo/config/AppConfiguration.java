package com.buyuphk.nettyclientdeomo.config;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/8/24 01:43
 * @since JDK 1.8
 */
public class AppConfiguration {

    private Long userId;

    private String userName;

    private String msgLoggerPath ;

    private String clearRouteUrl ;

    private long heartBeatTime ;

    private int errorCount ;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMsgLoggerPath() {
        return msgLoggerPath;
    }

    public void setMsgLoggerPath(String msgLoggerPath) {
        this.msgLoggerPath = msgLoggerPath;
    }


    public long getHeartBeatTime() {
        return heartBeatTime;
    }

    public void setHeartBeatTime(long heartBeatTime) {
        this.heartBeatTime = heartBeatTime;
    }


    public String getClearRouteUrl() {
        return clearRouteUrl;
    }

    public void setClearRouteUrl(String clearRouteUrl) {
        this.clearRouteUrl = clearRouteUrl;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
}
