package com.myrungo.rungo.shit;

import com.myrungo.rungo.cat.CatView;

import java.io.Serializable;

public class User implements Serializable {
    private CatView.Skins skin;
    private CatView.Heads head;
    private String current_challenge;
    private String name;
    private int age;
    private int rost;

    public CatView.Skins getSkin() {
        return skin;
    }

    public CatView.Heads getHead() {
        return head;
    }

    public User(CatView.Skins skin, CatView.Heads head) {
        this.skin = skin;
        this.head = head;
    }
}