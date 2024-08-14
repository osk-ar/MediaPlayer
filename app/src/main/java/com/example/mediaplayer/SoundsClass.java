package com.example.mediaplayer;

import android.net.Uri;

public class SoundsClass {
    public final int imageResId = R.drawable.background;
    public final Uri soundUri;
    public final String text;

    public SoundsClass(String text, Uri URI) {
        this.text = text;
        this.soundUri = URI;
    }

    public String getName(){
        return this.text;
    }

    public Uri getSound(){ return this.soundUri; }
}

