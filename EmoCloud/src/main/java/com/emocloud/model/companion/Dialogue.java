package com.emocloud.model.companion;

import com.emocloud.model.EmotionTag;
import com.emocloud.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
// 对话实体
@Entity
@Table(name = "dialogues")

public class Dialogue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //关联用户表：一个用户与一个大模型对话
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    //关联情绪标签表:多个对话共享同一情绪标签
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_tag_id")
    private EmotionTag emotionTag;

    @Column(nullable = false)
    private String mode; // CHATBOX 或 SCENE

    @Column(columnDefinition = "LONGTEXT")
    private String summary;

    @OneToMany(mappedBy = "dialogue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @OneToOne(mappedBy = "dialogue", cascade = CascadeType.ALL, orphanRemoval = true)
    private SceneMode sceneMode; // 场景模式属性（可选）

    @Column(nullable = false)
    //用来处理资源占用问题的
    //用户退出 APP 时，将对话标记为false（非活跃）
    //用户重新进入时，恢复最近一次的对话（isActive=true）
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;

    public Dialogue(Long id, User user, String mode, Boolean isActive, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.user = user;
        this.mode = mode;
        this.isActive = isActive;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Dialogue() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EmotionTag getEmotionTag() {
        return emotionTag;
    }

    public void setEmotionTag(EmotionTag emotionTag) {
        this.emotionTag = emotionTag;
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

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public SceneMode getSceneMode() {
        return sceneMode;
    }

    public void setSceneMode(SceneMode sceneMode) {
        this.sceneMode = sceneMode;
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