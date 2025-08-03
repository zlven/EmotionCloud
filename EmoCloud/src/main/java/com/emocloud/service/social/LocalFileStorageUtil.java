package com.emocloud.service.social ;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 本地文件存储工具类
 */
@Component
public class LocalFileStorageUtil {

    @Value("${image.upload-dir}")
    private String uploadDir;

    /**
     * 存储文件到本地并返回访问路径
     */
    public String storeFile(MultipartFile file, String fileName) throws IOException {
        // 创建上传目录
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 保存文件到本地
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        // 返回相对访问路径（需配合静态资源映射）
        return "/images/" + fileName;
    }

    /**
     * 删除本地文件
     */
    public void deleteFile(String filePath) {
        try {
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            Path path = Paths.get(uploadDir, fileName);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // 记录日志，忽略删除失败
        }
    }
}