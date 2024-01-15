package com.google.mediapipe.examples.facemesh;

import android.util.Log;

public class MyGlobals {
    private Integer score = 0;
    private String nickname;
    private String school;
    private String major;
    private String number;
    private String phone;
    private String mail;
    private String mate;
    private String eval;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
        Log.d("MyGlobals", "Score become: " + score);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMate() {
        return mate;
    }

    public void setMate(String mate) {
        this.mate = mate;
    }

    public String getEval() {
        return eval;
    }

    public void setEval(String eval) {
        this.eval = eval;
    }

    private static MyGlobals instance = null;

    public static synchronized MyGlobals getInstance() {
        if (null == instance) {
            instance = new MyGlobals();
        }
        return instance;
    }
}
