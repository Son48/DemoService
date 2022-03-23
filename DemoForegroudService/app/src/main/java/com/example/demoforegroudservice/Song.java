package com.example.demoforegroudservice;

import java.io.Serializable;

public class Song implements Serializable {
    private String titile;
    private String single;
    private int image;
    private int resource;

    public Song() {
    }

    public Song(String titile, String single, int image, int resource) {
        this.titile = titile;
        this.single = single;
        this.image = image;
        this.resource = resource;
    }

    public String getTitile() {
        return titile;
    }

    public void setTitile(String titile) {
        this.titile = titile;
    }

    public String getSingle() {
        return single;
    }

    public void setSingle(String single) {
        this.single = single;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }
}
