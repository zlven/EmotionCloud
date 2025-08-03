package com.emocloud.dto.EmotionLog;

import com.emocloud.dto.MusicDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class DiaryDTO implements Serializable {
    private Long id;
    private String title;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cratetime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatetime;
    private boolean sharetosocial;
    private List<String> images;
    private List<String> videos;
    private List<MusicDTO> music;
    private Long userId;
    private String font; // 新增：字体
    private String layout; // 排版（可扩展功能，默认default）
    private String decoration; // 装饰饰品

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getDecoration() {
        return decoration;
    }

    public void setDecoration(String decoration) {
        this.decoration = decoration;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCratetime() {
        return cratetime;
    }

    public void setCratetime(LocalDateTime cratetime) {
        this.cratetime = cratetime;
    }

    public LocalDateTime getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(LocalDateTime updatetime) {
        this.updatetime = updatetime;
    }

    public boolean isSharetosocial() {
        return sharetosocial;
    }

    public void setSharetosocial(boolean sharetosocial) {
        this.sharetosocial = sharetosocial;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getVideos() {
        return videos;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }

    public List<MusicDTO> getMusic() {
        return music;
    }

    public void setMusic(List<MusicDTO> music) {
        this.music = music;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}