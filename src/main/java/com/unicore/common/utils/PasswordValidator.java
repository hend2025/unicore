package com.unicore.common.utils;

/**
 * 密码复杂度验证工具类
 */
public class PasswordValidator {

    /**
     * 验证密码复杂度：至少8个字符，包含数字、字母、特殊符号中的至少3种
     * @param password 密码
     * @return 验证结果，null表示通过，否则返回错误信息
     */
    public static String validate(String password) {
        if (password == null || password.isEmpty()) {
            return "密码不能为空";
        }
        if (password.length() < 8) {
            return "密码长度至少8个字符";
        }
        
        int complexity = 0;
        // 检查是否包含数字
        if (password.matches(".*[0-9].*")) {
            complexity++;
        }
        // 检查是否包含字母
        if (password.matches(".*[a-zA-Z].*")) {
            complexity++;
        }
        // 检查是否包含特殊符号
        if (password.matches(".*[^0-9a-zA-Z].*")) {
            complexity++;
        }
        
        if (complexity < 3) {
            return "密码必须包含数字、字母、特殊符号";
        }
        
        return null; // 验证通过
    }
}
