package com.emocloud.model.pet;

import com.emocloud.model.User;
import jakarta.persistence.*;


@Entity
@Table(name = "pets")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String petname;
    @Column
    private int emotion;//情绪值，1-100
    @Column
    private Integer hungry;//饥饿值，1-100
    @Column
    private Integer Variety;//品种，猫狗兔子等，到时候再定吧

    // 外键关联到 User 表的 user_id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Pet() {

    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Pet(Long id, String petname, int emotion, Integer hungry, Integer variety, User user) {
        this.id = id;
        this.petname = petname;
        this.emotion = emotion;
        this.hungry = hungry;
        Variety = variety;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPetname() {
        return petname;
    }

    public void setPetname(String petname) {
        this.petname = petname;
    }

    public int getEmotion() {
        return emotion;
    }

    public void setEmotion(int emotion) {
        this.emotion = emotion;
    }

    public Integer getHungry() {
        return hungry;
    }

    public void setHungry(Integer hungry) {
        this.hungry = hungry;
    }

    public Integer getVariety() {
        return Variety;
    }

    public void setVariety(Integer variety) {
        Variety = variety;
    }
}