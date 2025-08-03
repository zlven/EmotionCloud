package com.emocloud.dto;

import java.io.Serializable;
import java.util.List;

public class UserDTO implements Serializable {
    private Long id;
    private String username;
    private String avatar;
    private Integer gender;
    private String password;
    private String signature;
    private Integer age;
    private String registertime;
    private List<Long> emotionLogIds;
    private List<Long> diaryIds;
    private Long dialogueId;
    private String dialogueMode;
    private String warning; // 用于传递警告信息

    // getters and setters
    public Long getDialogueId() {
        return dialogueId;
    }

    public void setDialogueId(Long dialogueId) {
        this.dialogueId = dialogueId;
    }

    public String getDialogueMode() {
        return dialogueMode;
    }

    public void setDialogueMode(String dialogueMode) {
        this.dialogueMode = dialogueMode;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getRegistertime() {
        return registertime;
    }

    public void setRegistertime(String registertime) {
        this.registertime = registertime;
    }

    public List<Long> getEmotionLogIds() {
        return emotionLogIds;
    }

    public void setEmotionLogIds(List<Long> emotionLogIds) {
        this.emotionLogIds = emotionLogIds;
    }

    public List<Long> getDiaryIds() {
        return diaryIds;
    }

    public void setDiaryIds(List<Long> diaryIds) {
        this.diaryIds = diaryIds;
    }
}