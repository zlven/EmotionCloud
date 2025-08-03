package com.emocloud.model;

import jakarta.persistence.*;

// 情绪标签实体
@Entity
@Table(name = "emotion_tags")
public class EmotionTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // 如：开心、难过、焦虑
    @Column
    //情绪值，理论上来说情绪值和name相关不应该出现在这里，但是使用了多次后续需要调整
    private Integer value;

    @Column(nullable = false)
    private String description;

    public EmotionTag(Long id, String name, Integer value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public EmotionTag() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}