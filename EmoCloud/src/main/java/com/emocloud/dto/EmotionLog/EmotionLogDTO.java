package com.emocloud.dto.EmotionLog;

import java.util.List;

/**
 * 情绪日志DTO（数据传输对象）：用于前后端数据交互，隐藏实体类细节
 */
public class EmotionLogDTO {
    private Long id; // 日志ID
    private String cratetime; // 生成时间（与实体类字段一致）
    private Integer emotionvalue; // 情绪值（与实体类字段一致）
    private String aigcchart; // 图片路径
    private String conclusion; // 日志结论
    private List<String> tags; // 标签列表
    private Long userId; // 用户ID（替代实体类中的User对象，避免循环引用）

    // getter和setter
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

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}