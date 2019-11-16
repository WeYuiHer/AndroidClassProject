//package com.example.testfromteacher;
//
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.util.Base64;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.scrat.app.selectorlibrary.ImageSelector;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.List;
//
//public class MainActivity extends AppCompatActivity {
//
//    private static final int REQUEST_CODE_SELECT_IMG = 1;
//    private static final int MAX_SELECT_COUNT = 9;
//
//    private TextView mContentTv;
//    private ImageView photo1, photo2;
//    private List<String> paths;
//    private Bitmap bm1,bm2;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        photo1 = findViewById(R.id.photo1);
//        photo2 = findViewById(R.id.photo2);
//
//        initView();
//    }
//
//    private void initView() {
//        mContentTv = (TextView) findViewById(R.id.content);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_CODE_SELECT_IMG) {
//            showContent(data);
//            return;
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    private void showContent(Intent data) {
//        paths = ImageSelector.getImagePaths(data);
//        System.out.println(paths);
//        if (paths.isEmpty()) {
//            mContentTv.setText(R.string.image_selector_select_none);
//            return;
//        }
//        mContentTv.setText(paths.toString());
//
//    }
//
//    //获取读取相册的权限
//
//    public void selectImg(View v) {
//            ImageSelector.show(this, REQUEST_CODE_SELECT_IMG, MAX_SELECT_COUNT);
//
//    }
//
//    public void showImg(View view) {
//        String[] permissions = new String[]{
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//        };
//        //进行sdcard的读写请求
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, permissions, 1);
//        } else {
//            show();
//        }
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case 1:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    show();
//                } else {
//                    Toast.makeText(this, "读相册的操作被拒绝", Toast.LENGTH_LONG).show();
//                }
//        }
//    }
//
//
//    private void show() {
//        int i;
//        for (i = 0; i < paths.size(); i++) {
//            if (i == 0 && photo1.getVisibility() == View.GONE) {
//                Bitmap bm = BitmapFactory.decodeFile(paths.get(i).toString());
//                photo1.setImageBitmap(bm);
//            } else if (i == 0 && photo1.getVisibility() == View.VISIBLE) {
//                Bitmap bm = BitmapFactory.decodeFile(paths.get(i).toString());
//                photo2.setImageBitmap(bm);
//            } else if (i == 1) {
//                Bitmap bm = BitmapFactory.decodeFile(paths.get(i).toString());
//                photo2.setImageBitmap(bm);
//            } else if (i == 0 && photo2.getVisibility() == View.VISIBLE) {
//                Bitmap bm = BitmapFactory.decodeFile(paths.get(i).toString());
//                photo1.setImageBitmap(bm);
//            }
//            if (i == 0) {
//                bm1 = BitmapFactory.decodeFile(paths.get(i).toString());
//                photo1.setImageBitmap(bm1);
//            } else if (i == 1) {
//                bm2 = BitmapFactory.decodeFile(paths.get(i).toString());
//                photo2.setImageBitmap(bm2);
//            }
//        }
//    }
//
//
//
//    //返回结果 弹窗
//
//    /*
//     * bitmap转base64
//     * */
//    private static String bitmapToBase64(Bitmap bitmap) {
//        String result = null;
//        ByteArrayOutputStream baos = null;
//        try {
//            if (bitmap != null) {
//                baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//
//                baos.flush();
//                baos.close();
//
//                byte[] bitmapBytes = baos.toByteArray();
//                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (baos != null) {
//                    baos.flush();
//                    baos.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return result;
//    }
//
//    public void facematch(View view) {
//    }
//    /*end*/
//
//
//}
