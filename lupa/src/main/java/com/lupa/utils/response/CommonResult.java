package com.lupa.utils.response;


import com.lupa.utils.exception.ExceptionCodeEnum;
import com.lupa.utils.exception.ExceptionHandler;

public class
CommonResult<T> {

    private long code;
    private String message;
    private T data;

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
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

    public CommonResult() {
    }

    public CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     */
    public static <T>CommonResult<T> success() {
        return new CommonResult<T>(ExceptionCodeEnum.SUCCESS.getCode(), ExceptionCodeEnum.SUCCESS.getMessage(),null);
    }

    /**
     * 成功返回结果
     */
    public static <T>CommonResult<T> success(String message) {
        return new CommonResult<T>(ExceptionCodeEnum.SUCCESS.getCode(),message,null);
    }

    /**
     * 成功返回结果
     */
    public static <T>CommonResult<T> success(T data) {
        return new CommonResult<T>(ExceptionCodeEnum.SUCCESS.getCode(),ExceptionCodeEnum.SUCCESS.getMessage(),data);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param  message 提示信息
     */
    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<T>(ExceptionCodeEnum.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     */
    public static <T> CommonResult<T> failed(ExceptionHandler errorCode) {
        System.out.println("errorCode1:" + errorCode);
        return new CommonResult<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static <T> CommonResult<T> failed(ExceptionHandler errorCode,String message) {
        System.out.println("errorCode2:" + errorCode);
        return new CommonResult<T>(errorCode.getCode(), message, null);
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static <T> CommonResult<T> failed(long errorCode,String message) {
        System.out.println("errorCode2:" + errorCode);
        return new CommonResult<T>(errorCode, message, null);
    }

    /**
     * 失败返回结果
     * @param message 提示信息
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(ExceptionCodeEnum.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed() {
        return failed(ExceptionCodeEnum.FAILED);
    }

}
