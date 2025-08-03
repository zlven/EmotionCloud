package com.emocloud.dto.Social;

import java.io.Serializable;

public class SocialPostDTO implements Serializable {
    private Long id;
    private String title;
    private String contents;
    private String crateTime;
    private String updateTime;
    private Integer commentCount;
    private Integer collectCount;
    private Integer likeCount;
    private String moodTag;
    private Integer shareCount;
    private Long publisherId;
    private String publisherAvatar;

    // Getters and Setters
    public Long getPostid() {
        return id;
    }

    public void setPostid(Long postid) {
        this.id = postid;
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

    public String getCrateTime() {
        return crateTime;
    }

    public void setCrateTime(String crateTime) {
        this.crateTime = crateTime;
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

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public String getPublisherAvatar() {
        return publisherAvatar;
    }

    public void setPublisherAvatar(String publisherAvatar) {
        this.publisherAvatar = publisherAvatar;
    }
    @Override
    public String toString() {
        return "SocialPostDTO{" +
                "title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", moodTag='" + moodTag + '\'' +
                ", userId=" + publisherId +
                '}';
    }
}