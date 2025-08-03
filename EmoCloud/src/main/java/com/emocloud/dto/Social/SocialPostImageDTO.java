package com.emocloud.dto.Social;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SocialPostImageDTO implements Serializable {
    private Long id;            // 图片ID
    private String imageUrl;    // 图片URL
    private String fileName;    // 文件名
    private String fileType;    // 文件类型
    private Long fileSize;      // 文件大小（字节）
    private LocalDateTime uploadTime; // 上传时间
    private Integer sortOrder;  // 图片排序
    private Long postId;        // 所属帖子ID

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}