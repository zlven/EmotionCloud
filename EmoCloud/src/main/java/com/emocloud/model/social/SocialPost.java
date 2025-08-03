package com.emocloud.model.social;

import com.emocloud.model.User;
import jakarta.persistence.*;


/**
 * 社交帖子实体类，对应数据库中的 social_posts 表
 */
@Entity
@Table(name = "social_posts")
public class SocialPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 帖子主键ID

    @Column
    private String title; // 帖子标题

    @Column
    private String contents; // 帖子内容

    @Column
    private String createTime; // 帖子创建时间（建议使用 LocalDateTime 类型）

    @Column
    private String updateTime; // 帖子更新时间（建议使用 LocalDateTime 类型）

    @Column
    private Integer commentCount; // 评论数

    @Column
    private Integer collectCount; // 收藏数

    @Column
    private Integer likeCount; // 点赞数

    @Column
    private String moodTag; // 心情标签

    @Column
    private Integer shareCount; // 分享数

    // 多对一关联：多个帖子属于同一个发布者
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 外键字段名
    private User publisher;

    public SocialPost() {
    }

    // 获取发布者头像的便捷方法
    public String getPublisherAvatar() {
        return publisher != null ? publisher.getAvatar() : null;
    }

    // 构造方法
    public SocialPost(Long id, String title, String contents, String createTime, String updateTime,
                      String moodTag, User publisher) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.moodTag = moodTag;
        this.publisher = publisher;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public String getMoodTag() {
        return moodTag;
    }

    public void setMoodTag(String moodTag) {
        this.moodTag = moodTag;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }

    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    @Override
    public String toString() {
        return "SocialPost{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", commentCount=" + commentCount +
                ", collectCount=" + collectCount +
                ", likeCount=" + likeCount +
                ", moodTag='" + moodTag + '\'' +
                ", shareCount=" + shareCount +
                ", publisher=" + publisher +
                '}';
    }
}