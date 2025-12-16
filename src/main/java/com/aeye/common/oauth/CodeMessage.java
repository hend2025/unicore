package com.aeye.common.oauth;

public class CodeMessage<T> {
    private int code;
    private String msg;
    private T data;

    public static CodeMessage AccessDenied = new CodeMessage(20003,"权限拒绝");
    public static CodeMessage NoLogin = new CodeMessage(20004,"未登录");
    public static CodeMessage ERROR = new CodeMessage(-1,"系统异常");
    public static CodeMessage SUCCESS = new CodeMessage(200,"处理成功");

    public CodeMessage(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
