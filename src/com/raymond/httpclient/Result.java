package com.raymond.httpclient;

import com.raymond.httpclient.enums.ErrorCode;

/**
 * 响应结果集
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-05 14:12
 */
public class Result<T> {
    private String code;

    private String message;

    private T data;

    /**
     * 默认网络错误
     */
    public Result() {
        this(ErrorCode.ERROR_CODE_800001, null);
    }

    /**
     * 默认值
     * @param errorCode 错误码
     */
    public Result(ErrorCode errorCode) {
        this(errorCode, null);
    }

    /**
     * 默认值
     * status:000
     * message:成功
     * @param data 传输的数据
     */
    public Result(T data) {
        this(ErrorCode.SUCCESS_CODE_000, data);
    }

    /**
     * @param errorCode 错误码
     * @param data 数据
     */
    public Result(ErrorCode errorCode, T data) {
        this.code = errorCode.getStrErrorCode();
        this.message = errorCode.getMessage();
        this.data = data;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
