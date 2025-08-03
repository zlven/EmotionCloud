package com.emocloud.model.companion;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
// 消息实体
@Entity
@Table(name = "messages")

public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //关联对话表：一个对话有多条消息
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dialogue_id", nullable = true)
    private Dialogue dialogue;

    @Column(nullable = false)
    private String senderType; // USER或AI

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String contentType; // TEXT、VOICE、IMAGE

    @Column(nullable = false)
    private Double emotionScore; // 情绪分数：用于帮助大模型判断用户情绪的

    @Column(nullable = false)
    private String functionCall; // 功能调用（如推荐歌单），外部扩展用

    @CreationTimestamp
    private LocalDateTime createTime;

    public Message(Long id, Dialogue dialogue, String senderType, String content, String contentType, LocalDateTime createTime) {
        this.id = id;
        this.dialogue = dialogue;
        this.senderType = senderType;
        this.content = content;
        this.contentType = contentType;
        this.createTime = createTime;
    }

    public Message() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Dialogue getDialogue() {
        return dialogue;
    }

    public void setDialogue(Dialogue dialogue) {
        this.dialogue = dialogue;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Double getEmotionScore() {
        return emotionScore;
    }

    public void setEmotionScore(Double emotionScore) {
        this.emotionScore = emotionScore;
    }

    public String getFunctionCall() {
        return functionCall;
    }

    public void setFunctionCall(String functionCall) {
        this.functionCall = functionCall;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}