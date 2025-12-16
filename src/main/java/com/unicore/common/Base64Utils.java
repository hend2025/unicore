package com.unicore.common;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64编解码工具类
 */
public class Base64Utils {

    /**
     * Base64解码
     * @param encoded Base64编码的字符串
     * @return 解码后的字符串
     */
    public static String decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return encoded;
        }
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encoded);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            // 如果解码失败，返回原字符串（兼容未编码的情况）
            return encoded;
        }
    }

    /**
     * Base64编码
     * @param raw 原始字符串
     * @return Base64编码后的字符串
     */
    public static String encode(String raw) {
        if (raw == null || raw.isEmpty()) {
            return raw;
        }
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
}
