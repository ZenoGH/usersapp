package com.usersapp;

public class User {
    private String name;
    private String login;
    private String password;

    public boolean getNext() {
        return false;
    }

    public User(String name, String login, String password) {
        this.name = name;
        this.login = login;
        this.password = password;
    }
}