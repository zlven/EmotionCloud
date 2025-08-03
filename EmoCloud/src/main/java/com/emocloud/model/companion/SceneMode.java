package com.emocloud.model.companion;

import jakarta.persistence.*;
// 场景模式属性表
@Entity
@Table(name = "scene_modes")

public class SceneMode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dialogue_id", nullable = true, unique = true)
    private Dialogue dialogue;

    @Column(nullable = false)
    private String sceneName;//场景名称

    @Column(nullable = false)
    private String backgroundImage;

    @Column(nullable = false)
    private String backgroundMusic;

    @Column(nullable = false)
    private String npcRole;


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

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getBackgroundMusic() {
        return backgroundMusic;
    }

    public void setBackgroundMusic(String backgroundMusic) {
        this.backgroundMusic = backgroundMusic;
    }

    public String getNpcRole() {
        return npcRole;
    }

    public void setNpcRole(String npcRole) {
        this.npcRole = npcRole;
    }

}