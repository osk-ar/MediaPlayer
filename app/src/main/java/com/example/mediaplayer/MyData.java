package com.example.mediaplayer;

public class MyData {
    public final int imageResId = R.drawable.background;
    public final  int soundId;

    public String text;
    public MyData(String text, int id) {
        this.text = text;
        this.soundId = id;
    }

    public String getName(){
        return this.text;
    }

    public int getSound(){ return this.soundId; }
}

