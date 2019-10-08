package com.buyuphk.nettyclientdeomo.vo.res;

public class RegisterUserResVO {
    private int code;
    private String message;
    private int reqNo;
    private DataBody dataBody;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getReqNo() {
        return reqNo;
    }

    public void setReqNo(int reqNo) {
        this.reqNo = reqNo;
    }

    public DataBody getDataBody() {
        return dataBody;
    }

    public void setDataBody(DataBody dataBody) {
        this.dataBody = dataBody;
    }

    public class DataBody {
        private long userId;
        private String userName;

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
