package com.emocloud.model;

import com.emocloud.model.emolog.Diary;
import com.emocloud.model.emolog.EmotionLog;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//用户唯一标识
    @Column
    private String username;//用户名称
    @Column
    private String avatar;//用户的头像，但是应该是url，到时候看看怎么改
    @Column
    private Integer gender;//用户性别(0男 1女 2无性别)
    @Column
    private String password;//用户的密码，到时候要加密，现在先不加密
    @Column
    private String signature;//用户的非主流签名
    @Column
    private Integer age;//用户年龄
    @Column
    private String registertime;//用户注册时间，到时候统一格式所以用long

    // 用户与情绪日志的关联，一个用户可以有多个情绪日志
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmotionLog> emotionLogs;

    // 用户与情绪日记本的关联，一个用户可以有多个情绪日记本
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Diary> diaries;

    // 用户与宠物的关联，一个用户只可以有一个宠物哦

    public User() {

    }

    // Getters and Setters
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

    public List<EmotionLog> getEmotionLogs() {
        return emotionLogs;
    }

    public void setEmotionLogs(List<EmotionLog> emotionLogs) {
        this.emotionLogs = emotionLogs;
    }

    public List<Diary> getDiaries() {
        return diaries;
    }

    public void setDiaries(List<Diary> diaries) {
        this.diaries = diaries;
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

    public User(Long id, String username, String avatar, Integer gender, String password, String signature, Integer age, String registertime, List<EmotionLog> emotionLogs, List<Diary> diaries) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.gender = gender;
        this.password = password;
        this.signature = signature;
        this.age = age;
        this.registertime = registertime;
        this.emotionLogs = emotionLogs;
        this.diaries = diaries;
    }
}