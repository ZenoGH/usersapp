package com.usersapp;

import java.util.Date;

public class Payload {
    private int id;
    private User info;
    private String date;

    public Payload(String name, String login, String password) {
        info = new User(name, login, password);
    }

    public int getId() {
        return id;
    }

    public User getInfo() {
        return info;
    }

    public String getDate() {
        return date;
    }
}



