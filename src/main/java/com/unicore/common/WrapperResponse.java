package com.unicore.common;

import lombok.Data;
import java.io.Serializable;

@Data
public class WrapperResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int code = 0;
    private String type;
    private String message;
    private T data;

    public static <T> WrapperResponse<T> success() {
        return success(null);
    }

    public static <T> WrapperResponse<T> success(T data) {
        WrapperResponse<T> response = new WrapperResponse<>();
        response.setCode(0);
        response.setType("success");
        response.setMessage("操作成功");
        response.setData(data);
        return response;
    }

    public static <T> WrapperResponse<T> success(String message, T data) {
        WrapperResponse<T> response = new WrapperResponse<>();
        response.setCode(0);
        response.setType("success");
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> WrapperResponse<T> error(String message) {
        return error(500, message);
    }

    public static <T> WrapperResponse<T> error(int code, String message) {
        WrapperResponse<T> response = new WrapperResponse<>();
        response.setCode(code);
        response.setType("error");
        response.setMessage(message);
        return response;
    }
}
