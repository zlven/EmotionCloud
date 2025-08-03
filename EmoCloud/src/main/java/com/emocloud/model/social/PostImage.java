package com.emocloud.model.social;

import com.emocloud.model.social.SocialPost;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.persistence.*;
/**
 * 帖子图片实体类，对应数据库中的 social_post_images 表
 * 每个图片属于一个特定的社交帖子
 */
@Entity
@Table(name = "social_post_images")
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 图片主键ID

    @Column(nullable = false)
    private String imageUrl; // 图片URL

    @Column(nullable = false)
    private String fileName; // 原始文件名

    @Column(nullable = false)
    private String fileType; // 文件类型（如jpg、png）

    @Column(nullable = false)
    private Long fileSize; // 文件大小（字节）

    @Column(nullable = false)
    private LocalDateTime uploadTime; // 上传时间

    @Column(nullable = false)
    private Integer sortOrder; // 图片排序序号

    // 多对一关联：多个图片属于同一个帖子
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private SocialPost post; // 所属帖子

    public PostImage() {
    }

    // 构造方法
    public PostImage(String imageUrl, String fileName, String fileType, Long fileSize,
                     SocialPost post, Integer sortOrder) {
        this.imageUrl = imageUrl;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadTime = LocalDateTime.now();
        this.post = post;
        this.sortOrder = sortOrder;
    }

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

    public SocialPost getPost() {
        return post;
    }

    public void setPost(SocialPost post) {
        this.post = post;
    }
}