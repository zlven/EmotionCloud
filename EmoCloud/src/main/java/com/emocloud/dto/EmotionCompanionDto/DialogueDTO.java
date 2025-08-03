package com.emocloud.dto.EmotionCompanionDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class DialogueDTO implements Serializable {
    private Long id;
    private Long userId;
    private Long emotionTagId;
    private String mode;
    private String summary;
    private List<Long> messageIds;
    private Long sceneModeId;
    private Boolean isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEmotionTagId() {
        return emotionTagId;
    }

    public void setEmotionTagId(Long emotionTagId) {
        this.emotionTagId = emotionTagId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<Long> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(List<Long> messageIds) {
        this.messageIds = messageIds;
    }

    public Long getSceneModeId() {
        return sceneModeId;
    }

    public void setSceneModeId(Long sceneModeId) {
        this.sceneModeId = sceneModeId;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}