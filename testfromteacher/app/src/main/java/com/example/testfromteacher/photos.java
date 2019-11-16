package com.example.testfromteacher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class photos extends AppCompatActivity {
    private String getphotos="http://112.124.46.47:8000/getphotos";
    private String photos_result;
    private static final int COMPLETED = 0;
    private ArrayList<String> photo_base64_list = new ArrayList<>();
    private  ArrayList<Bitmap> photo_bitmap_array=new ArrayList<>();
    private ArrayList<Album_item> photoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        final String message = intent.getStringExtra(AlbumAdapter.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        //TextView textView = findViewById(R.id.textView);
        //textView.setText(message);
        new Thread(){
            @Override
            public void run(){
                    String url=getphotos+"?userid="+message;
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)//url是http://112.124.46.47:8000/getphotos
                            .get()
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        photos_result=response.body().string();
                        Log.i("查询结果",photos_result);
                        photo_base64_list=JArray_to_List(photos_result);
                        for (int i=0;i<photo_base64_list.size();i++){
                            Bitmap bitmap=base64_to_bitmap(photo_base64_list.get(i));
                            photo_bitmap_array.add(bitmap);
                            if(i==photo_base64_list.size()-1){
                                Message msg = new Message();
                                msg.what = COMPLETED;
                                handler.sendMessage(msg);
                            }
                        }
//                        Bitmap bitmap=base64_to_bitmap(img_result);
//                        album_bitarray.add(bitmap);
//
//                        //以下两句代替了initAlbums()的for循环
//                        Album_item ai = new Album_item(al.get(i), bitmap);
//                        albumList.add(ai);
//
//                        if(i==al.size()-1){
//                            //......处理比较耗时的操作  
//                            //处理完成后给handler发送消息  

//                        Bundle bundle = new Bundle();  //得到bundle对象
//                        bundle.putString("img_result",img_result);  //key-"sff",通过key得到value-"value值"(String型)
//                        msg.setData(bundle);

//                        }
                        //Log.i("数据", img_result);
                        //user_list=JArray_to_List(user_id_result);//user_list有size属性
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("失败", e.toString() + "....");
                    }
                }

        }.start();
    }
    private void initAlbums() {
        for(int i=0;i<photo_bitmap_array.size();i++) {
            Album_item ai = new Album_item("嘻嘻", photo_bitmap_array.get(i));
            photoList.add(ai);
        }
    }
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
                initAlbums();
                RecyclerView recyclerView = findViewById(R.id.photo_recycler_view);
                //LinearLayoutManager layoutManager = new LinearLayoutManager(myalbum.this);
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
//                layoutManager.setOrientation(RecyclerView.VERTICAL);
//                recyclerView.setLayoutManager(layoutManager);
                AlbumAdapter adapter = new AlbumAdapter(photoList);
                recyclerView.setAdapter(adapter);
            }
        }
    };
}
