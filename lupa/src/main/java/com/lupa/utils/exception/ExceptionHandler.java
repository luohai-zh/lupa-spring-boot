package com.lupa.utils.exception;

public interface ExceptionHandler{

    /**
     * 错误码
     * @return
     */
    long getCode();

    /**
     * 错误描述
     * @return
     */
    String getMessage();

}
