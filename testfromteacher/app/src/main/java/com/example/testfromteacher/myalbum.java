package com.example.testfromteacher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class myalbum extends AppCompatActivity {
    private ArrayList<Album_item> albumList = new ArrayList<>();
    private ImageView photo;
    private static final int COMPLETED = 0;
    private String getuserid,getimg,user_id_result,img_result;
    private ArrayList<String> user_list = new ArrayList<>();

    private  ArrayList<Bitmap> album_bitarray=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myalbum);
        photo=findViewById(R.id.photo);
        fresh();

    }
    @Override
    protected void onResume() {
        super.onResume();
//        albumList=new ArrayList<>();
//        fresh();

    }
    public void fresh(){
        getuserid="http://112.124.46.47:8000/getuserid";
        getimg="http://112.124.46.47:8000/getimg";
        new Thread(){
            @Override
            public void run(){
                String rs = baiduapi.getuserlist("test");
                Log.i("rs from client detect:",rs);
                Gson g = new Gson();
                JsonObject obj = g.fromJson(rs, JsonObject.class);
                JsonArray temp=obj.get("result").getAsJsonObject().get("user_id_list").getAsJsonArray();
                user_list=JArray_to_List(temp);
                getimgs(user_list);
//                OkHttpClient client = new OkHttpClient();
//                Request request = new Request.Builder()
//                        .url(getuserid)//url是http://112.124.46.47:8000/getuserid
//                        .get()
//                        .build();
//                try {
//                    Response response = client.newCall(request).execute();
//                    user_id_result=response.body().string();
//                    Log.i("数据", user_id_result);
//                    user_list=JArray_to_List(user_id_result);//user_list有size属性
//                    getimgs(user_list);
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.i("失败", e.toString() + "....");
//                }

            }
        }.start();
    }
//这个函数就把我获取到的数据扔进去就行了，我现在只获取了base64，name可以用静态的测试一下。
    private void initAlbums() {
        for(int i=0;i<album_bitarray.size();i++) {
            Album_item ai = new Album_item("嘻嘻", album_bitarray.get(i));
            albumList.add(ai);
        }
    }
    /**
     * 判断变量类型方法
     */
    public static String getType(Object o) {
        return o.getClass().toString(); //使用int类型的getClass()方法
    }
    /**
     * 把服务器返回的jsonarray字符串处理成arraylist
     */
    public ArrayList<String> JArray_to_List(String jarray_s){
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray Jarray = parser.parse(jarray_s).getAsJsonArray();
        ArrayList<String> lcs = new ArrayList<String>();
        for(JsonElement obj : Jarray ){
            String cse = gson.fromJson( obj , String.class);
            lcs.add(cse);
        }
        return lcs;
    }
    public ArrayList<String> JArray_to_List(JsonArray jarray_s){
        ArrayList<String> lcs = new ArrayList<String>();
        for(int i=0;i<jarray_s.size();i++ ){
            String cse = jarray_s.get(i).getAsString();
            lcs.add(cse);
        }
        return lcs;
    }

    /**
     * 数据初始化
     */
    public void getimgs(final ArrayList<String> al){
//        for (int i=0;i<al.size();i++){
//            final String url=getimg+"?userid="+al.get(i);
            new Thread(){
                @Override
                public void run(){
                    for (int i=0;i<al.size();i++){
                        String url=getimg+"?userid="+al.get(i);
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(url)//url是http://112.124.46.47:8000/upload
                                .get()
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            img_result=response.body().string();
                            Bitmap bitmap=base64_to_bitmap(img_result);
                            album_bitarray.add(bitmap);

                            //以下两句代替了initAlbums()的for循环
                            Album_item ai = new Album_item(al.get(i), bitmap);
                            albumList.add(ai);

                            if(i==al.size()-1){
                                //......处理比较耗时的操作  
                                //处理完成后给handler发送消息  
                        Message msg = new Message();
                        msg.what = COMPLETED;
//                        Bundle bundle = new Bundle();  //得到bundle对象
//                        bundle.putString("img_result",img_result);  //key-"sff",通过key得到value-"value值"(String型)
//                        msg.setData(bundle);
                        handler.sendMessage(msg);
                            }
                            //Log.i("数据", img_result);
                            //user_list=JArray_to_List(user_id_result);//user_list有size属性
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.i("失败", e.toString() + "....");
                        }
                    }


                }
            }.start();


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
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            Bundle bundle=msg.getData();
//            String img_result = bundle.getString("img_result");
//            Bitmap bitmap=base64_to_bitmap(img_result);
//            if (msg.what == COMPLETED) {
//                photo.setImageBitmap(bitmap);
//            }
            if (msg.what == COMPLETED) {
                //initAlbums();
                RecyclerView recyclerView = findViewById(R.id.recycler_view);
                //LinearLayoutManager layoutManager = new LinearLayoutManager(myalbum.this);
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);

//                layoutManager.setOrientation(RecyclerView.VERTICAL);
//                recyclerView.setLayoutManager(layoutManager);

                AlbumAdapter adapter = new AlbumAdapter(albumList);
                recyclerView.setAdapter(adapter);
            }
        }
    };


    public void to_selectphoto(View view) {
        Intent intent = new Intent(this,selctphoto.class);
        startActivityForResult(intent,1);
        //startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            albumList=new ArrayList<>();
            fresh(); //刷新操作

        }
    }

}