package com.lupa.utils.exception;




import com.lupa.utils.response.CommonResult;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 拦截表单参数校验
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({BindException.class})
    public CommonResult bindException(BindException e)
    {
       BindingResult bindingResult = e.getBindingResult();
       String bindExceptionMessage=bindingResult.getFieldError().getField()+"字段错误信息："+Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
        return CommonResult.failed(ExceptionCodeEnum.VALIDATE_FAILED,bindExceptionMessage);
    }

    /**
     * 拦截JSON参数校验
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult bindException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        String bindExceptionMessage=bindingResult.getFieldError().getField()+"字段错误信息："+Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
        return CommonResult.failed(ExceptionCodeEnum.VALIDATE_FAILED,bindExceptionMessage);
    }

    /**
     * 拦截参数类型不正确
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public CommonResult bindException(HttpMediaTypeNotSupportedException e){
        return CommonResult.failed(ExceptionCodeEnum.NUMBER_FORMAT_FAILED, Objects.requireNonNull(e.getMessage()));
    }

    /**
     * 拦截参数类型不正确
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NumberFormatException.class)
    public CommonResult bindException(NumberFormatException e){
        return CommonResult.failed(ExceptionCodeEnum.PRAM_NOT_MATCH, Objects.requireNonNull(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BusinessException.class)
    public CommonResult bindException(BusinessException e){
        return CommonResult.failed(e.getCode(),e.getMessage());
    }
    /**
     * 声明要捕获的异常
     * @param e
     * @param <T>
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public <T>CommonResult<?> defaultExceptionHandler(Exception e){
        e.printStackTrace();
        if (e instanceof BusinessException){
            return CommonResult.failed(ExceptionCodeEnum.FAILED,Objects.requireNonNull(e.getMessage()));
        }
        //参数校验异常
        if (e instanceof MissingServletRequestParameterException){
            return CommonResult.failed(ExceptionCodeEnum.PRAM_NOT_MATCH,Objects.requireNonNull(e.getMessage()));
        }
        // 未知错误
        return CommonResult.failed(ExceptionCodeEnum.ERROR, e.getMessage());
    }
}
