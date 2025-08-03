package com.emocloud.model.emolog;

import com.emocloud.model.User;
import jakarta.persistence.*;
import java.util.List;

/**
 * 情绪日志实体类（与数据库表emotion_logs映射）
 */
@Entity
@Table(name = "emotion_logs")
public class EmotionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键ID

    @Column
    private String cratetime; // 日志生成时间（格式：yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss）

    @Column
    private Integer emotionvalue; // 情绪值（1-100）

    @Column
    private String aigcchart; // AIGC生成的图片路径（默认为空）

    @Column
    private String conclusion; // 日志结论（核心内容）

    @Column(name = "tags")
    @ElementCollection // 存储标签列表（多对多关系的简化）
    private List<String> tags; // 标签列表

    // 多对一关联：多个日志属于一个用户
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 关联数据库字段：user_id
    private User user; // 关联的用户对象

    // 全参构造器
    public EmotionLog(Long id, String cratetime, Integer emotionvalue, String aigcchart, String conclusion, List<String> tags, User user) {
        this.id = id;
        this.cratetime = cratetime;
        this.emotionvalue = emotionvalue;
        this.aigcchart = aigcchart;
        this.conclusion = conclusion;
        this.tags = tags;
        this.user = user;
    }

    // 无参构造器（JPA要求）
    public EmotionLog() {}

    // getter和setter（必须实现，否则DTO转换会失败）
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCratetime() { return cratetime; }
    public void setCratetime(String cratetime) { this.cratetime = cratetime; }

    public Integer getEmotionvalue() { return emotionvalue; }
    public void setEmotionvalue(Integer emotionvalue) { this.emotionvalue = emotionvalue; }

    public String getAigcchart() { return aigcchart; }
    public void setAigcchart(String aigcchart) { this.aigcchart = aigcchart; }

    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}