package com.techtrickbd.nshop.models;

public class Profile {
    private String name;
    private String email;
    private Integer coin;
    private String uid;

    public Profile() {
    }

    public Profile(String name, String email, Integer coin, String uid) {
        this.name = name;
        this.email = email;
        this.coin = coin;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
