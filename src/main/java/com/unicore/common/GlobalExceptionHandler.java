package com.unicore.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 允许向前端展示的安全错误消息白名单
    private static final Set<String> SAFE_ERROR_MESSAGES = new HashSet<>(Arrays.asList(
            "用户不存在", "原密码错误", "密码不能为空", "密码长度至少8个字符",
            "密码必须包含数字、字母、特殊符号", "admin用户不能被禁用", "admin用户不能被删除",
            "参数不完整", "文件不能为空", "文件大小不能超过50MB", "非法文件名",
            "文件必须有扩展名", "不允许上传此类型文件", "文件类型与内容不匹配"
    ));

    @ExceptionHandler(Exception.class)
    public WrapperResponse<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return WrapperResponse.error("系统异常，请联系管理员");
    }

    @ExceptionHandler(RuntimeException.class)
    public WrapperResponse<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        String message = e.getMessage();
        // 只返回白名单中的错误消息，防止敏感信息泄露
        if (message != null && isSafeMessage(message)) {
            return WrapperResponse.error(message);
        }
        return WrapperResponse.error("操作失败，请稍后重试");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public WrapperResponse<Void> handleUsernameNotFoundException(UsernameNotFoundException e) {
        // 不暴露具体是用户名还是密码错误，防止用户枚举攻击
        return WrapperResponse.error("用户名或密码错误");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public WrapperResponse<Void> handleBadCredentialsException(BadCredentialsException e) {
        return WrapperResponse.error("用户名或密码错误");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public WrapperResponse<Void> handleAccessDeniedException(AccessDeniedException e) {
        return WrapperResponse.error(403, "没有权限访问此资源");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public WrapperResponse<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return WrapperResponse.error("上传文件大小超出限制");
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

    /**
     * 检查错误消息是否安全可展示
     */
    private boolean isSafeMessage(String message) {
        for (String safe : SAFE_ERROR_MESSAGES) {
            if (message.contains(safe)) {
                return true;
            }
        }
        return false;
    }
}
