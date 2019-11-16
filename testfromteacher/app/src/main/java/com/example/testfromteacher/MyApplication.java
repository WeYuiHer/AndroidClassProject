package com.example.testfromteacher;

import android.app.Application;

public class MyApplication extends Application {
    private String base64;
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    public String getBAse64() {
        return base64;
    }

    public void setBase64(String s) {
        this.base64 = s;
    }

}
