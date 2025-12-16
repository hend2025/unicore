package com.unicore.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public WrapperResponse<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return WrapperResponse.error("系统异常，请联系管理员");
    }

    @ExceptionHandler(RuntimeException.class)
    public WrapperResponse<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return WrapperResponse.error(e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public WrapperResponse<Void> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return WrapperResponse.error(e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public WrapperResponse<Void> handleBadCredentialsException(BadCredentialsException e) {
        return WrapperResponse.error("用户名或密码错误");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public WrapperResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null 
            ? e.getBindingResult().getFieldError().getDefaultMessage() 
            : "参数校验失败";
        return WrapperResponse.error(message);
    }

    @ExceptionHandler(BindException.class)
    public WrapperResponse<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldError() != null 
            ? e.getBindingResult().getFieldError().getDefaultMessage() 
            : "参数绑定失败";
        return WrapperResponse.error(message);
    }
}
