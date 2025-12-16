package com.unicore.controller;

import cn.hutool.core.util.IdUtil;
import com.unicore.common.WrapperResponse;
import com.unicore.config.MinioConfig;
import com.unicore.entity.SysOss;
import com.unicore.mapper.SysOssMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private SysOssMapper ossMapper;

    @PostMapping("/upload")
    public WrapperResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = IdUtil.fastSimpleUUID() + suffix;

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            String fileUrl = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + fileName;

            SysOss oss = new SysOss();
            oss.setFileName(fileName);
            oss.setOrigName(originalFilename);
            oss.setFileSuffix(suffix);
            oss.setFileUrl(fileUrl);
            oss.setService("minio");
//            oss.setCrteId(SecurityUtils.getUserId());
//            oss.setCrteName(SecurityUtils.getUserName());
            oss.setCrteTime(new Date());
            ossMapper.insert(oss);

            Map<String, String> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("url", fileUrl);
            return WrapperResponse.success(result);
        } catch (Exception e) {
            return WrapperResponse.error("文件上传失败: " + e.getMessage());
        }
    }
}
