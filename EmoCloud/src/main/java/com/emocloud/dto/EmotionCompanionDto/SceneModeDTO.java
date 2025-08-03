package com.emocloud.dto.EmotionCompanionDto;


import java.io.Serializable;

public class SceneModeDTO implements Serializable {
    private Long id;
    private Long dialogueId;
    private String sceneName;
    private String backgroundImage;
    private String backgroundMusic;
    private String npcRole;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDialogueId() {
        return dialogueId;
    }

    public void setDialogueId(Long dialogueId) {
        this.dialogueId = dialogueId;
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