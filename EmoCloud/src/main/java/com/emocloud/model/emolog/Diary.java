package com.emocloud.model.emolog;

import com.emocloud.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "diaries")
public class Diary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String title;//应该要有默认的标题
    @Column(columnDefinition = "LONGTEXT")
    private String content;//日记内容
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime cratetime; // 创建时间
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatetime; // 更新时间
    @Column
    private boolean sharetosocial;//是否分享到情绪广场，是1，否0
    @ElementCollection
    private List<String> images;//插入的图片

    @ElementCollection
    private List<String> videos;//插入的视频
    @Column

    private String font; // 新增：字体
    @Column

    private String layout; // 排版（可扩展功能，默认default）
    @Column

    private String decoration; // 装饰饰品

//***********可能最终会添加其他元素比如排版类型、装饰物等等******************

    // 情绪日记本与用户的关联，多个情绪日记本属于一个用户
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Diary() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Diary(Long id, String title, String content, boolean sharetosocial, List<String> images, List<String> videos, User user) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.sharetosocial = sharetosocial;
        this.images = images;
        this.videos = videos;

        this.user = user;
    }

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
}