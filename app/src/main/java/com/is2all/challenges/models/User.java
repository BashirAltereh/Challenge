package com.is2all.challenges.models;

public class User {
    private String email,name,id,pushId;


    public User(){

    }
    public User(String email, String name, String id,String pushId) {
        this.email = email;
        this.name = name;
        this.id = id;
        this.pushId = pushId;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
