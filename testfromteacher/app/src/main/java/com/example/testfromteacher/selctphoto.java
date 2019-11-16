package com.example.testfromteacher;

import android.Manifest;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.icu.util.Calendar;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class selctphoto extends AppCompatActivity {
    private String TAG=selctphoto.class.getName();
    private Uri imgUri; //记录拍照后的照片文件的地址(临时文件)
    private ArrayList<Uri> uris;
    private ImageView photo,match_photo1,match_photo2;
    private EditText groupid,userid,userinfo;
    private String face_token,age,beauty,gender,emotion,expression,group_id,user_id,user_info,score,search_face_token;
    private String search_result="";
    private boolean flag=false;
    private String uploadFileName;
    private  String date="";
    private byte[] fileBuf;
    private String base64="";
    private ArrayList<String> match_base64;
    private ArrayList<byte[]> match_fileBuf;
    private ArrayList<Bitmap> match_bitmap;
    private static final int COMPLETED = 0;
    private tobase64 test= new tobase64();
    private String uploadUrl = "http://112.124.46.47:8000/img";
    private String searchUrl = "http://112.124.46.47:8000/search";
    private String url = "http://www.google.com";

    private MyApplication application;
    //private String url ="https://aip.baidubce.com/rest/2.0/face/v3/detect";
    //private String url = "http://112.124.46.47";


    @Override
    //实例化按钮和图片
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (MyApplication) this.getApplicationContext();
        setContentView(R.layout.activity_selctphoto);
        photo = findViewById(R.id.photo);
        match_photo1 = findViewById(R.id.match_photo1);
        match_photo2 = findViewById(R.id.match_photo2);
        groupid=findViewById(R.id.groupid);
        userid=findViewById(R.id.userid);
        userinfo=findViewById(R.id.userinfo);
        uris=new ArrayList<>();
        match_base64=new ArrayList<>();
        match_fileBuf=new ArrayList<>();
        match_bitmap=new ArrayList<>();
//        gender_list=d_v.gender_list_init();
//        emotion_list=d_v.emotion_list_init();
//        expression_list=d_v.expression_list_init();
        Button button_detect=findViewById(R.id.detect);
        Button button_add=findViewById(R.id.add);
        Button button_search=findViewById(R.id.search);
        Button button_compare=findViewById(R.id.compare);
        button_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detect();
            }
        });
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add(1,base64,groupid.getText().toString(),userid.getText().toString(),userinfo.getText().toString());
            }
        });
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiFaceSearch();

            }
        });
        button_compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                match();
            }
        });
        Button button_recognize=findViewById(R.id.recognize);
        button_recognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognize();
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    //按钮点击事件，获取sdcard读写权限并且调用打开相册的操作
    public void select(View view) {
        uris=new ArrayList<>();
        match_base64=new ArrayList<>();
        match_fileBuf=new ArrayList<>();
        match_bitmap=new ArrayList<>();
        match_photo1.setVisibility(View.GONE);
        match_photo2.setVisibility(View.GONE);
        String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    //进行sdcard的读写请求
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            openGallery(); //打开相册，进行选择
        }
    }


    @Override
    //获取读取相册的权限
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Toast.makeText(this, "读相册的操作被拒绝", Toast.LENGTH_LONG).show();
                }
        }
    }

    //打开相册,进行照片的选择
    private void openGallery(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        //intent.putExtra();
        intent.putExtra(intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, 1);
    }

    @Override
    //选择照片成功，调用handleSelect函数
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                handleSelect(data);
                Log.i("onActivityResult", "resultCode为1 ");
                break;
            case 2:
                //此时，相机拍照完毕
                if(resultCode==RESULT_OK){
                    try {
//                        InputStream inputStream = getContentResolver().openInputStream(imgUri);
//                        fileBuf = convertToBytes(inputStream);
//                        inputStream.close();
//                        base64 = Base64Util.encode(fileBuf);
//                        application.setBase64(base64);
                        int degree=readPictureDegree(imgUri.getPath());
                        //利用ContentResolver,查询临时文件，并使用BitMapFactory,从输入流中创建BitMap
                        //同样需要配合Provider,在Manifest.xml中加以配置
                        InputStream inputStream = getContentResolver().openInputStream(imgUri);
                        fileBuf = convertToBytes(inputStream);
                        inputStream.close();
                        base64 = Base64Util.encode(fileBuf);
                        application.setBase64(base64);
                        inputStream = getContentResolver().openInputStream(imgUri);
                        Bitmap map=rotateToDegrees(BitmapFactory.decodeStream(inputStream),degree);
                        photo.setImageBitmap(map);
                        photo.setVisibility(View.VISIBLE);
                        inputStream.close();
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            case 3:
                if(resultCode==RESULT_OK){
                    base64="";
                    photo.setVisibility(View.GONE);

                }
        }
    }
    public int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    public Bitmap rotateToDegrees(Bitmap tmpBitmap, int degrees) {

        Matrix matrix = new Matrix();

        //matrix.reset();

        matrix.setRotate(degrees);

        Bitmap b = Bitmap.createBitmap(tmpBitmap, 0, 0, tmpBitmap.getWidth(), tmpBitmap.getHeight(), matrix,

                true);
        return b;

    }


    //选择后照片的读取工作，根据获得的intent data，把图片变成位图设置到前端显示，以及我自己写的，获取图片的base64编码
    private void handleSelect(Intent intent) {
        Cursor cursor = null;

        Uri uri = intent.getData();
        if (uri == null){
            //取多张图的uri
            ClipData clipData = intent.getClipData();
            int count = clipData.getItemCount();
            for (int i = 0;i < count; i++){
                ClipData.Item item = clipData.getItemAt(i);
                //Uri uri1 = item.getUri();
                uris.add(item.getUri());
                Log.i("多张图",uris.get(i).toString());
            }
            for (int i=0;i<uris.size();i++){
                cursor = getContentResolver().query(uris.get(i), null, null, null, null);
//                if (cursor.moveToFirst()) {
//                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
//                    uploadFileName = cursor.getString(columnIndex);
//                }
                try {
                    InputStream inputStream1 = getContentResolver().openInputStream(uris.get(i));
                    InputStream inputStream2 = getContentResolver().openInputStream(uris.get(i));
                    match_base64.add(test.test4(inputStream2));
                    Log.i("base64", match_base64.get(i));
                    match_fileBuf.add(convertToBytes(inputStream1));
                    match_bitmap.add(BitmapFactory.decodeByteArray(match_fileBuf.get(i), 0, match_fileBuf.get(i).length));
                    set_match_phopt();
//                    photo.setImageBitmap(bitmap);
//                    photo.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cursor.close();
            }
        }else {
            //取单张图的uri
            //uris.add(uri);
            Log.i("单张图",uri.toString());
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                uploadFileName = cursor.getString(columnIndex);
            }
            try {
                InputStream inputStream1 = getContentResolver().openInputStream(uri);
                InputStream inputStream2 = getContentResolver().openInputStream(uri);
                base64= test.test4(inputStream2);
                application.setBase64(base64);
                Log.i("base64", base64);
                fileBuf=convertToBytes(inputStream1);
                Bitmap bitmap = BitmapFactory.decodeByteArray(fileBuf, 0, fileBuf.length);
                photo.setImageBitmap(bitmap);
                photo.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cursor.close();
        }




    }
    public void set_match_phopt(){
        photo.setVisibility(View.GONE);
        match_photo1.setImageBitmap(match_bitmap.get(0));
        match_photo2.setImageBitmap(match_bitmap.get(1));
        match_photo1.setVisibility(View.VISIBLE);
        match_photo2.setVisibility(View.VISIBLE);
    }


    //文件上传的处理，使用okhttp3来进行http请求，使用服务器上的app.js来进行请求的响应
    //public void upload(View view) {
public void upload(final int type,final String gid, final String uid, final String uinfo) {
    date=getdate();
    // 使用 toString() 函数显示日期时间
    //System.out.println(date.toString());
        new Thread() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                //上传文件域的请求体部分body，fileBuf是二进制字节流，设置类型为图片
                RequestBody formBody = RequestBody
                        .create(fileBuf,MediaType.parse("image/jpeg"));
                //整个上传的请求体部分（普通表单+文件上传域）
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("title", "Square Logo")
                        //.addFormDataPart("groupid",groupid.getText().toString())
                        //.addFormDataPart("userid",userid.getText().toString())
                        //.addFormDataPart("userinfo",userinfo.getText().toString())
                        .addFormDataPart("groupid",gid)
                        .addFormDataPart("userid",uid)
                        .addFormDataPart("userinfo",uinfo)
                        .addFormDataPart("face_token",face_token)
                        .addFormDataPart("upload_time",date)
                        //filename:avatar,originname:abc.jpg
                        .addFormDataPart("avatar", uploadFileName, formBody)
                        .build();
                Request request = new Request.Builder()
                        .url(uploadUrl)//url是http://112.124.46.47:8000/upload
                        .post(requestBody)//requestBody是上面定义的
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    Log.i("数据", response.body().string() + "....");
                    Log.i("body", requestBody.toString());
                    //......处理比较耗时的操作  
                    //处理完成后给handler发送消息
                    if(type==1){
                        Message msg = new Message();
                        msg.what = COMPLETED;
                        handler.sendMessage(msg);
                    }else if(type==2){
                        flag=false;
                    }




                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("失败", e.toString() + "....");
                }
            }
        }.start();
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {
                groupid.setText("");
                userid.setText("");
                userinfo.setText("");
                groupid.setVisibility(View.GONE);
                userid.setVisibility(View.GONE);
                userinfo.setVisibility(View.GONE);
                photo.setVisibility(View.GONE);
                base64="";
                showalert("提示","上传成功！");
            }
        }
    };
    private byte[] convertToBytes(InputStream inputStream) throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        inputStream.close();
        return  out.toByteArray();
    }

    public void add(final int type, final String b64, final String gid, final String uid, final String uinfo) {//向人脸库添加新的人脸
        if(type==1){
            if(match_base64.size()!=0){
                showalert("警告","只支持单张图片上传，请重新选择。");
                return;
            }
            else if(b64.equals("")){
                showalert("注意","请先选择图片！");
                return;
            }
            else if(groupid.getVisibility()==View.GONE){
                groupid.setVisibility(View.VISIBLE);
                userid.setVisibility(View.VISIBLE);
                userinfo.setVisibility(View.VISIBLE);
                return;
            }
            else if(groupid.getText().toString().equals("")||userid.getText().toString().equals("")||userinfo.getText().toString().equals("")){
                showalert("注意","请填写人物信息");
                return;
            }
        }
            new Thread(){
                @Override
                public void run() {

                    String rs=baiduapi.addFaceWithBase64(b64,gid,uid,uinfo);//这些应该来自于edittext
                    Gson g = new Gson();
                    JsonObject obj = g.fromJson(rs, JsonObject.class);

                    if (obj.get("error_code").getAsString().equals("223105")) {//error_code=0的话就是成功。这里表示已经有了
                        if(type==1) {//手动注册，已经添加过，则弹出警告框
                            Looper.prepare();
                            showalert("警告", "这张图片已存在，请勿重复上传。");
                            Looper.loop();
                            groupid.setText("");
                            userid.setText("");
                            userinfo.setText("");
                        }
                        else if(type==2){//自动注册，百度有，数据库无
                            face_token=search_face_token;
                            upload(type,gid,uid,uinfo);
                        }
                    }
                    else {//百度无，数据库理论上应该也无
                        if(type==1) {//手动注册，通过百度add得到的face_token传到数据库
                            face_token = obj.get("result").getAsJsonObject().get("face_token").getAsString();
                        }
                        else if(type==2){//自动注册，通过百度search得到的face_token传到数据库
                            face_token=search_face_token;
                        }
                        upload(type,gid,uid,uinfo);

                    }
                    Log.i("请求百度API的返回值：",rs);
                }
            }.start();


    }
    //detect
    public void detect() {
        if(groupid.getVisibility()==View.VISIBLE){
            groupid.setVisibility(View.GONE);
            userid.setVisibility(View.GONE);
            userinfo.setVisibility(View.GONE);
        }
        if(match_base64.size()!=0){
            showalert("警告","只支持单张图片检测，请重新选择。");
            return;
        }
        else if(base64.equals("")){
            showalert("注意","请先选择图片!");
            return;
        }
        else {
            Intent intent = new Intent(this,CanvasDemoActivity.class);
            //intent.putExtra("String",base64);
            startActivityForResult(intent,3);
        }
    }


    public void multiFaceSearch(){
        if(groupid.getVisibility()==View.VISIBLE){
            groupid.setVisibility(View.GONE);
            userid.setVisibility(View.GONE);
            userinfo.setVisibility(View.GONE);
        }
        //   final String imageBase64 = Base64Util.encode(fileBuf);
        final String[] msg = {null};
        if(match_base64.size()!=0){
            showalert("警告","只支持单张图片搜索，请重新选择。");
            return;
        }
        else if (base64.equals("")) {
            showalert("注意", "请先选择图片!");
        }
        else {
        new Thread() {
            @Override
            public void run() {
                msg[0] = FaceSearch.mulitFaceSearch(base64);
            }
        }.start();

        while (msg[0] == null) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("多人脸检测结果： " + msg[0]);

            JSONObject jsonObject = JSONObject.parseObject(msg[0]);
            String success = jsonObject.getString("error_msg");
            String toastInfo = "";

        if (success.equals("SUCCESS")) {
            JSONObject jsonObject1 = jsonObject.getJSONObject("result");
            int faceNum = jsonObject1.getInteger("face_num");
            StringBuilder sb = new StringBuilder("图片中有" + faceNum + "张人脸:\r\n");
            JSONArray jsonArray = jsonObject1.getJSONArray("face_list");
            for (int i = 0; i < faceNum; i++) {
                if(!(jsonArray.getJSONObject(i).getJSONArray("user_list").isEmpty()))
                {
                JSONObject object = jsonArray.getJSONObject(i).getJSONArray("user_list").getJSONObject(0);
                System.out.println(object.toString());
                Integer score = object.getInteger("score");
                if (score > 80) {
                    flag=true;
                    search_face_token= jsonArray.getJSONObject(i).getString("face_token");
                    group_id=object.getString("group_id");
                    user_id=object.getString("user_id");
                    user_info=object.getString("user_info");
                    sb.append("用户信息为：" +user_info + "  匹配度为:" + score + "  \r\n");
                    is_face_in_library(search_face_token,user_id);
                    while (flag){
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                }
            }
            toastInfo = sb.toString();
            if (faceNum == 0)
                toastInfo = "未找到匹配的人脸信息。";

        } else {
            toastInfo = jsonObject.getString("error_msg");
        }
        System.out.println(toastInfo + "================================");
        returnResult(toastInfo);
    }
        /*Looper.prepare();
        show_search_result("人脸搜索结果",toastInfo);
        Looper.loop();*/
    }
    public void showalert(String title,String message){
        AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                .setTitle(title)//标题
                .setMessage(message)//内容
                .create();
        alertDialog1.show();
    }
//    public void show_detect_result(String title,String age,String beauty,String gender,String emotion,String expression){
//        AlertDialog alertDialog1 = new AlertDialog.Builder(this)
//                .setTitle(title)//标题
//                .setMessage("性别："+gender+"\n年龄："+age+"\n颜值（满分100）："+beauty+"\n表情："+expression+"\n情绪："+emotion)//内容
//                .create();
//        alertDialog1.show();
//    }

    public void show_search_result(String title,String groupid,String userid,String userinfo,String score){
        AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                .setTitle(title)//标题
                .setMessage("所属用户组："+groupid+"\n用户ID："+userid+"\n用户信息："+userinfo+"\n匹配得分（满分100）："+score)//内容
                .create();
        alertDialog1.show();
    }
    public void show_search_result(String title,String result){
        AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                .setTitle(title)//标题
                .setMessage(result)//内容
                .create();
        alertDialog1.show();
    }
    public void match(){
        if(match_base64.size()==0||match_base64.size()==1){
            uris=new ArrayList<>();
            match_base64=new ArrayList<>();
            match_fileBuf=new ArrayList<>();
            match_bitmap=new ArrayList<>();
            showalert("注意","需要选择两张图片！");
            return;
        }
        new Thread() {
            @Override
            public void run() {
                //String base64=baiduapi.toBase64(Thread.currentThread().getContextClassLoader().getResourceAsStream(uploadFileName));
                String rs = baiduapi.faceCompare(match_base64.get(0),match_base64.get(1));//这些应该来自于edittext
                Log.i("对比结果",rs);
                Gson g = new Gson();
                //JsonObject obj = g.fromJson(rs, JsonObject.class).get("result").getAsJsonObject().get("score").getAsDouble();
                float score=(float)g.fromJson(rs, JsonObject.class).get("result").getAsJsonObject().get("score").getAsDouble();
                Looper.prepare();
                showalert("人脸对比结果","两张人脸相似度为："+score+"%");
                Looper.loop();
            }
        }.start();



    }
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == COMPLETED) {
//                groupid.setText("");
//                userid.setText("");
//                userinfo.setText("");
//                groupid.setVisibility(View.GONE);
//                userid.setVisibility(View.GONE);
//                userinfo.setVisibility(View.GONE);
//                photo.setVisibility(View.GONE);
//                base64="";
//            }
//        }
//    };
    public  String getdate(){
        Calendar cd = Calendar.getInstance();
        String year= Integer.toString(cd.get(Calendar.YEAR));
        String month=Integer.toString(cd.get(Calendar.MONTH)+1);
        String day=Integer.toString(cd.get(Calendar.DATE));
        String hour=Integer.toString(cd.get(Calendar.HOUR));
        String min=Integer.toString(cd.get(Calendar.MINUTE));
        String sec=Integer.toString(cd.get(Calendar.SECOND));
        String time=year+'-'+month+'-'+day+' '+hour+':'+min+':'+sec;
        return time;
    }
    public void is_face_in_library(final String string1,final String string2){
        new Thread(){
            @Override
            public void run(){
                    String url=searchUrl+"?face_token="+string1+"&userid="+string2;
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)//url是http://112.124.46.47:8000/upload
                            .get()
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        search_result=response.body().string();
                        if (search_result.equals("[]"))
                            add(2,base64,group_id,user_id,user_info);
                        else {
                            flag=false;
                        }
                        Log.i("face_token存在吗",search_result);


                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("失败", e.toString() + "....");
                    }



            }
        }.start();

    }
    /**
     * 按钮事件处理
     */
    public void camera(View view) throws Exception{

        //删除并创建临时文件，用于保存拍照后的照片
        //android 6以后，写Sdcard是危险权限，需要运行时申请，但此处使用的是"关联目录"，无需！
        File outImg=new File(getExternalCacheDir(),"temp.jpg");
        if(outImg.exists())
            outImg.delete();
        outImg.createNewFile();

        //复杂的Uri创建方式
        if(Build.VERSION.SDK_INT>=24)
            //这是Android 7后，更加安全的获取文件uri的方式（需要配合Provider,在Manifest.xml中加以配置）
            imgUri= FileProvider.getUriForFile(this,"cn.wzy.app1.fileprovider",outImg);
        else
            imgUri=Uri.fromFile(outImg);

        //利用actionName和Extra,启动《相机Activity》
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
        startActivityForResult(intent,2);
        //到此，启动了相机，等待用户拍照
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    //返回结果 弹窗
    public void returnResult(String msg) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder.setTitle("结果");
        builder.setMessage(msg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //System.out.println("点了确定");
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // System.out.println("点了取消");

            }
        });
        //一样要show
        builder.show();

    }


    public void recognize(){
        //   final String imageBase64 = Base64Util.encode(fileBuf);
        final String [] msg={null};
        if (base64 .equals("")) {
            showalert("注意", "请先选择图片!");
        }
        else {
            new Thread() {
                @Override
                public void run() {
                    String access_token = null;
                    OkHttpClient okHttpClient = new OkHttpClient();
                    //构建一个请求对象
                    String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&&client_id=piyjjLHgHrmdGfAupf7LfZsX&client_secret=LfCeI0ynnklqjv26TE84Br7vPsyCpvdx";
                    Request request1 = new Request.Builder().url(url).build();
                    try {
                        Response response1 = okHttpClient.newCall(request1).execute();
                        String result=response1.body().string();
                        JSONObject obj = JSON.parseObject(result);
                        access_token = obj.getString("access_token");
                    }catch(Exception e){

                    }
                    String image=null;
                    try {
                        image = URLEncoder.encode(base64, "utf-8");
                    }catch (Exception e){

                    }
                    String content = "image=" +image;;
                    RequestBody body = FormBody.create(MediaType.parse("application/x-www-form-urlencoded"), content);
                    Request request = new Request.Builder()
                            .url("https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general?access_token=" +access_token)
                            .post(body)
                            .build();
                    try {
                        Response response = okHttpClient.newCall(request).execute();
                        String res = response.body().string();
                        msg[0] = res;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
            while (msg[0] == null) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            StringBuilder sb=new StringBuilder();
            JSONObject object=JSON.parseObject(msg[0]);
            JSONArray array=object.getJSONArray("result");
            if(array.size()>=1){
                JSONObject object1=array.getJSONObject(0);
                sb.append("类别："+object1.getString("root")+";详情："+object1.getString("keyword")+"；相似度："+object1.getFloat("score"));
            }else{
                sb.append("识别未成功");
            }
            returnResult(sb.toString());
        }}
}
