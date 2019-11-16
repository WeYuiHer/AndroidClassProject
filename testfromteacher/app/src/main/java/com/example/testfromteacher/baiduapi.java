package com.example.testfromteacher;

import android.util.Log;

import com.example.testfromteacher.utils.GsonUtils;
import com.example.testfromteacher.utils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class baiduapi {
    static String detectUrl="https://aip.baidubce.com/rest/2.0/face/v3/detect?access_token=";
    static String addUrl="https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add?access_token=";
    static String searchUrl="https://aip.baidubce.com/rest/2.0/face/v3/search?access_token=";
    static String getusersUrl="https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/getusers?access_token=";
    static String faceCompareUrl="https://aip.baidubce.com/rest/2.0/face/v3/match?access_token=";

    static  String sss="";

    static String access_token="24.8efc179d4414b574b905afe16aa66690.2592000.1576238509.282335-17649816";//使用properties文件存储易变常量，并获取access_token
    static {
//        try {
//            Properties properties=new Properties();
//            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("access_token.properties"));
//            access_token=properties.getProperty("access_token");
            detectUrl+=access_token;
            addUrl+=access_token;
            searchUrl+=access_token;
            getusersUrl+=access_token;
            faceCompareUrl+=access_token;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    public static String toBase64(InputStream inputStream){
        String s= null;
        try {
            byte[] buf=new byte[inputStream.available()];
            inputStream.read(buf);
            //s = new BASE64Encoder().encode(buf);
            s=new String(Base64.getEncoder().encode(buf));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  s;
    }


    public static String detectFaceWithUrl(String faceUrl){
        String rs="";
        OkHttpClient client = new OkHttpClient();
        RequestBody body=new FormBody.Builder()
                .add("image_type","URL")
                .add("image",faceUrl)
                .build();
        Request request=new Request.Builder()
                .url(detectUrl)
                .header("Content-Type","application/json")
                .post(body)
                .build();

        try {
            Response resp= client.newCall(request).execute();
            rs = resp.body().string();
            Log.i("rs from detect:",rs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rs;
    }


    public static String detectFaceWithBase64(String Base64){
        String rs="";
        OkHttpClient client = new OkHttpClient();
        RequestBody body=new FormBody.Builder()
                .add("image_type","BASE64")
                .add("image",Base64)
                .add("face_field","age,beauty,expression,face_shape,gender,emotion,landmark")
                .build();
        Request request=new Request.Builder()
                .url(detectUrl)
                .header("Content-Type","application/json")
                .post(body)
                .build();

        try {
            Response resp= client.newCall(request).execute();
            rs = resp.body().string();
            System.out.println(rs);
            //Log.i("rs from baidu detect:",rs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public static String addFaceWithBase64(String Base64,String groupId,String userId,String userInfo){
        String rs="";
        OkHttpClient client = new OkHttpClient();
        RequestBody body=new FormBody.Builder()
                .add("image_type","BASE64")
                .add("image",Base64)
                .add("group_id",groupId)
                .add("user_id",userId)
                .add("user_info",userInfo)
                .build();
        Request request=new Request.Builder()
                .url(addUrl)
                .header("Content-Type","application/json")
                .post(body)
                .build();

        try {
            Response resp= client.newCall(request).execute();
            rs = resp.body().string();
            Gson g = new Gson();
            JsonObject obj = g.fromJson(rs, JsonObject.class);
            Log.i("错误码:",obj.get("error_code").getAsString());
            //Log.i("baiduapi返回数据", obj.get("result").getAsJsonObject().get("face_token").getAsString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return rs;
    }


    public static String searchFaceWithBase64(String Base64,String groupIdList){
        String rs="";
        OkHttpClient client = new OkHttpClient();
        RequestBody body=new FormBody.Builder()
                .add("image_type","BASE64")
                .add("image",Base64)
                .add("group_id_list", groupIdList)
                .build();
        Request request=new Request.Builder()
                .url(searchUrl)
                .header("Content-Type","application/json")
                .post(body)
                .build();

        try {
            Response resp= client.newCall(request).execute();
            rs = resp.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rs;
    }
    public static String getuserlist(String group_id){
        String rs="";
        OkHttpClient client = new OkHttpClient();
        RequestBody body=new FormBody.Builder()
                .add("group_id",group_id)
                .build();
        Request request=new Request.Builder()
                .url(getusersUrl)
                .header("Content-Type","application/json")
                .post(body)
                .build();
        try {
            Response resp= client.newCall(request).execute();
            rs = resp.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public static String faceCompare(String face1,String face2){
        String rs="";

        Map<String, Object> map1 = new HashMap<>();
        map1.put("image",face1);
        map1.put("image_type","BASE64");


        Map<String, Object> map2 = new HashMap<>();
        map2.put("image",face2);
        map2.put("image_type","BASE64");


        ArrayList<Map> map = new ArrayList<>();
        map.add(map1);
        map.add(map2);
        String param = GsonUtils.toJson(map);
        Log.i("param",param);
        try {
            rs = HttpUtil.post(faceCompareUrl, access_token, "application/json", param);
            System.out.println(rs+"********************************");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }


    public static void main(String[] args) throws Exception{
        String s=toBase64(Thread.currentThread().getContextClassLoader().getResourceAsStream("cage1.jpeg"));
        System.out.println(s);

    }
}
