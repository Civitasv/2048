package com.husen.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity
public class User {
    @Id
    @Property(nameInDb = "_id")
    private Long id;

    private String nickName;

    private String signName;

    private String sex;

    private String phone;

    private int maxScore;

    private int score;

    private String arrayStr;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    private String uri;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public String getArrayStr() {
        return arrayStr;
    }

    public void setArrayStr(String arrayStr) {
        this.arrayStr = arrayStr;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Generated(hash = 133189799)
    public User(Long id, String nickName, String signName, String sex, String phone, int maxScore,
            int score, String arrayStr, String uri) {
        this.id = id;
        this.nickName = nickName;
        this.signName = signName;
        this.sex = sex;
        this.phone = phone;
        this.maxScore = maxScore;
        this.score = score;
        this.arrayStr = arrayStr;
        this.uri = uri;
    }

    @Generated(hash = 586692638)
    public User() {
    }


}
