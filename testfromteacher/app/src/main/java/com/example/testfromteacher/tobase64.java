package com.example.testfromteacher;

import android.util.Log;

import java.io.InputStream;
import java.util.Base64;

public class tobase64 {
    public String test4(InputStream inputs) throws Exception{
        //InputStream inputStream=new FileInputStream("/Users/johnyu/Documents/abc.JPG");
        //InputStream inputStream=inputs;
        //OutputStream outputStream=new FileOutputStream("/Users/johnyu/Documents/abc1.JPG");
        //文件读入缓存并编码
        byte[] buf=new byte[inputs.available()];
        inputs.read(buf);
        //编码
        String s=new String(Base64.getEncoder().encode(buf));
        inputs.close();
        Log.i("base64编码为",s);
        return  s;

        //解码，并写入文件
        //byte[] buf1= Base64.getDecoder().decode(s);
        //outputStream.write(buf1);

        //outputStream.close();


    }
}
