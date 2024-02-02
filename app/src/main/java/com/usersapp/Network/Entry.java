package com.usersapp.Network;


import com.usersapp.User;

public class Entry {
    private int id;
    private User info;
    private String date;

    public Entry(String name, String login, String password) {
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



