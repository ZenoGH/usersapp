package com.usersapp;

import java.util.Date;

public class Payload {
    private int id;
    private User info;
    private Date date;

    public Payload(String name, String login, String password) {
        info = new User(name, login, password);
    }
}



