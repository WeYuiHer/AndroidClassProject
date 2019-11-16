package com.example.testfromteacher;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class CanvasDemoActivity extends AppCompatActivity {
    private MyApplication application;
    private String base64;
    private Thread mThread;
    private String age,beauty,gender,emotion,expression;
    private JsonObject gender_list = new JsonObject();
    private JsonObject emotion_list = new JsonObject();
    private JsonObject expression_list= new JsonObject();
    private detect_value d_v=new detect_value();
    //private float left,top,right,bottom,width,height;
    private boolean flag;
    private JsonArray landmark;
    private ArrayList<Float> x=new ArrayList<>();
    private ArrayList<Float> y=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x=new ArrayList<>();
        y=new ArrayList<>();
        application=(MyApplication) this.getApplicationContext();

        gender_list=d_v.gender_list_init();
        emotion_list=d_v.emotion_list_init();
        expression_list=d_v.expression_list_init();
        //Intent intent = getIntent();
        base64=application.getBAse64();

    }
    @Override
    protected void onStart(){
        super.onStart();
        //System.out.println("结果："+base64);
        detect();
    }
    public Bitmap base64_to_bitmap(String string){
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;

    }
    public void show_detect_result(String title,String age,String beauty,String gender,String emotion,String expression){
        AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage("性别："+gender+"\n年龄："+age+"\n颜值（满分100）："+beauty+"\n表情："+expression+"\n情绪："+emotion)
                .create();
        alertDialog1.show();

    }
    public void detect(){

        mThread=new Thread() {
            @Override
            public void run(){
                String rs = baiduapi.detectFaceWithBase64(base64);
                //System.out.println(rs);
                Log.i("rs from client detect:",rs);
                Gson g = new Gson();
                JsonObject obj = g.fromJson(rs, JsonObject.class).get("result").getAsJsonObject().get("face_list").getAsJsonArray().get(0).getAsJsonObject();
                age=obj.get("age").getAsString();
                beauty=obj.get("beauty").getAsString();
                gender=gender_list.get(obj.get("gender").getAsJsonObject().get("type").getAsString()).getAsString();
                emotion=emotion_list.get(obj.get("emotion").getAsJsonObject().get("type").getAsString()).getAsString();
                expression=expression_list.get(obj.get("expression").getAsJsonObject().get("type").getAsString()).getAsString();
                landmark=obj.get("landmark72").getAsJsonArray();
                System.out.println("检测结果：年龄"+age+"性别"+gender+"颜值"+beauty+"emotion"+emotion+"expression"+expression);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setContentView(new myView(getApplicationContext()));
                    }
                });
            }
        };
        mThread.start();
    }
    private class myView extends View {
//        private int imageWidth, imageHeight;
        Bitmap myBitmap;

        public myView(Context context){   //view类的构造函数
            super(context);
            BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
            BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565; //构造位图生成的参数，必须为565。类名+enum
            myBitmap = base64_to_bitmap(base64);
//            imageWidth = myBitmap.getWidth();
//            imageHeight = myBitmap.getHeight();
        }
        //override函数
        protected void onDraw(Canvas canvas){
            Paint p = new Paint();
            canvas.drawBitmap(myBitmap, 0,0, p);
            p.setColor(Color.GREEN);// 设置颜色
            p.setStrokeWidth(5);//设置线宽
            p.setStyle(Paint.Style.STROKE);//设置不填满

                    //百度api返回的72个点
                    for(int i=0;i<72;i++){
                        x.add((float)landmark.get(i).getAsJsonObject().get("x").getAsDouble());
                        y.add((float)landmark.get(i).getAsJsonObject().get("y").getAsDouble());
                    }
                    Path path_face = new Path();
                    path_face.moveTo(x.get(0), y.get(0));// 此点为多边形的起点
                    for(int i=1;i<=12;i++)
                    {
                        path_face.lineTo(x.get(i), y.get(i));
                    }
                    //path.close(); // 使这些点构成封闭的多边形
                    canvas.drawPath(path_face, p);

                    Path path_left_brow = new Path();
                    path_left_brow.moveTo(x.get(22), y.get(22));// 此点为多边形的起点
                    for(int i=23;i<=29;i++)
                    {
                        path_left_brow.lineTo(x.get(i), y.get(i));
                    }
                    path_left_brow.close(); // 使这些点构成封闭的多边形
                    canvas.drawPath(path_left_brow, p);

                    Path path_right_brow = new Path();
                    path_right_brow.moveTo(x.get(39), y.get(39));// 此点为多边形的起点
                    for(int i=40;i<=46;i++)
                    {
                        path_right_brow.lineTo(x.get(i), y.get(i));
                    }
                    path_right_brow.close(); // 使这些点构成封闭的多边形
                    canvas.drawPath(path_right_brow, p);

                    Path path_left_eye = new Path();
                    path_left_eye.moveTo(x.get(13), y.get(13));// 此点为多边形的起点
                    for(int i=14;i<=20;i++)
                    {
                        path_left_eye.lineTo(x.get(i), y.get(i));
                    }
                    path_left_eye.close(); // 使这些点构成封闭的多边形
                    canvas.drawPath(path_left_eye, p);

                    Path path_right_eye = new Path();
                    path_right_eye.moveTo(x.get(30), y.get(30));// 此点为多边形的起点
                    for(int i=31;i<=37;i++)
                    {
                        path_right_eye.lineTo(x.get(i), y.get(i));
                    }
                    path_right_eye.close(); // 使这些点构成封闭的多边形
                    canvas.drawPath(path_right_eye, p);

                    Path path_nose = new Path();
                    path_nose.moveTo(x.get(47), y.get(47));// 此点为多边形的起点
                    for(int i=48;i<=56;i++)
                    {
                        path_nose.lineTo(x.get(i), y.get(i));
                    }
                    path_nose.close(); // 使这些点构成封闭的多边形
                    canvas.drawPath(path_nose, p);

                    Path path_mouth_outside = new Path();
                    path_mouth_outside.moveTo(x.get(58), y.get(58));// 此点为多边形的起点
                    for(int i=59;i<=65;i++)
                    {
                        path_mouth_outside.lineTo(x.get(i), y.get(i));
                    }
                    path_mouth_outside.close(); // 使这些点构成封闭的多边形
                    canvas.drawPath(path_mouth_outside, p);

                    Path path_mouth_inside = new Path();
                    path_mouth_inside.moveTo(x.get(58), y.get(58));// 此点为多边形的起点
                    for(int i=66;i<=71;i++)
                    {
                        path_mouth_inside.lineTo(x.get(i), y.get(i));
                        if(i==68){path_mouth_inside.lineTo(x.get(62), y.get(i));}
                    }
                    path_mouth_inside.close(); // 使这些点构成封闭的多边形
                    canvas.drawPath(path_mouth_inside, p);
                    show_detect_result("人脸检测结果",age,beauty,gender,emotion,expression);


        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mThread.isAlive()){
            mThread.interrupt();
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
        }
        return super.onKeyDown(keyCode, event);
    }

}
