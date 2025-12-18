package com.unicore.controller;

import cn.hutool.core.util.IdUtil;
import cn.hsa.hsaf.core.framework.web.WrapperResponse;
import com.unicore.config.MinioConfig;
import com.unicore.entity.SysOss;
import com.unicore.mapper.SysOssMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequestMapping("/api/file")
public class FileController {

    // 允许上传的文件类型白名单
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".pdf",
            ".jpg", ".jpeg", ".png", ".gif", ".bmp",
            ".txt", ".log", ".zip", ".rar"
    ));

    // 禁止上传的危险文件类型
    private static final Set<String> DANGEROUS_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".exe", ".bat", ".cmd", ".sh", ".ps1", ".vbs", ".js", ".jsp", ".php",
            ".asp", ".aspx", ".war", ".jar", ".class", ".dll", ".so"
    ));

    // 最大文件大小 150MB
    private static final long MAX_FILE_SIZE = 150 * 1024 * 1024;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private SysOssMapper ossMapper;

    @PostMapping("/upload")
    public WrapperResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        try {
            // 安全校验：检查文件是否为空
            if (file == null || file.isEmpty()) {
                return WrapperResponse.fail("文件不能为空", null);
            }

            // 安全校验：检查文件大小
            if (file.getSize() > MAX_FILE_SIZE) {
                return WrapperResponse.fail("文件大小不能超过150MB", null);
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return WrapperResponse.fail("文件名不能为空", null);
            }

            // 安全校验：防止路径遍历攻击
            if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
                return WrapperResponse.fail("非法文件名", null);
            }

            // 获取文件扩展名并转小写
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex < 0) {
                return WrapperResponse.fail("文件必须有扩展名", null);
            }
            String suffix = originalFilename.substring(lastDotIndex).toLowerCase();

            // 安全校验：检查是否为危险文件类型
            if (DANGEROUS_EXTENSIONS.contains(suffix)) {
                return WrapperResponse.fail("不允许上传此类型文件", null);
            }

            // 安全校验：检查文件类型白名单
            if (!ALLOWED_EXTENSIONS.contains(suffix)) {
                return WrapperResponse.fail("不支持的文件类型，允许的类型：" + String.join(", ", ALLOWED_EXTENSIONS), null);
            }

            // 安全校验：验证Content-Type与扩展名是否匹配
            String contentType = file.getContentType();
            if (!isValidContentType(suffix, contentType)) {
                return WrapperResponse.fail("文件类型与内容不匹配", null);
            }

            // 生成安全的文件名（UUID + 扩展名）
            String fileName = IdUtil.fastSimpleUUID() + suffix;

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(contentType)
                    .build());

            String fileUrl = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + fileName;

            SysOss oss = new SysOss();
            oss.setFileName(fileName);
            oss.setOrigName(sanitizeFilename(originalFilename));
            oss.setFileSuffix(suffix);
            oss.setFileUrl(fileUrl);
            oss.setService("minio");
            oss.setCrteTime(new Date());
            ossMapper.insert(oss);

            Map<String, String> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("url", fileUrl);
            return WrapperResponse.success(result);
        } catch (Exception e) {
            // 不暴露详细错误信息
            return WrapperResponse.fail("文件上传失败，请稍后重试", null);
        }
    }

    /**
     * 验证Content-Type与文件扩展名是否匹配
     */
    private boolean isValidContentType(String suffix, String contentType) {
        if (contentType == null) return false;
        contentType = contentType.toLowerCase();
        switch (suffix) {
            case ".jpg":
            case ".jpeg":
                return contentType.contains("image/jpeg");
            case ".png":
                return contentType.contains("image/png");
            case ".gif":
                return contentType.contains("image/gif");
            case ".bmp":
                return contentType.contains("image/bmp");
            case ".pdf":
                return contentType.contains("application/pdf");
            case ".doc":
                return contentType.contains("application/msword");
            case ".docx":
                return contentType.contains("application/vnd.openxmlformats");
            case ".xls":
                return contentType.contains("application/vnd.ms-excel");
            case ".xlsx":
                return contentType.contains("application/vnd.openxmlformats");
            case ".txt":
            case ".log":
                return contentType.contains("text/plain");
            case ".zip":
                return contentType.contains("application/zip") || contentType.contains("application/x-zip");
            case ".rar":
                return contentType.contains("application/x-rar") || contentType.contains("application/octet-stream");
            default:
                return true;
        }
    }

    /**
     * 清理文件名中的特殊字符
     */
    private String sanitizeFilename(String filename) {
        if (filename == null) return "";
        return filename.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5._-]", "_");
    }
}
