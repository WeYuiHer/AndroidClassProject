package com.example.testfromteacher;

import android.graphics.Bitmap;

public class Album_item {
    private String name;
    private int imageId;
    private Bitmap bitmap;

    public Album_item(String name, Bitmap bitmap) {
        this.name = name;
        //this.imageId = imageId;
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

//    public int getImageId() {
//        return imageId;
//    }

    public Bitmap getBitmap() { return bitmap; }
}
