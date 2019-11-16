package com.example.testfromteacher;


import com.example.testfromteacher.utils.GsonUtils;
import com.example.testfromteacher.utils.HttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 人脸搜索
 */
public class FaceSearch {

    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    // 请求url

    static String url = "https://aip.baidubce.com/rest/2.0/face/v3/search";
    static String url2 = "https://aip.baidubce.com/rest/2.0/face/v3/multi-search";

    public static String faceSearch(String imageBase64) {

        try {
            Map<String, Object> map = new HashMap<>();
            //map.put("image", "027d8308a2ec665acb1bdf63e513bcb9");
            map.put("image",imageBase64);
            map.put("liveness_control", "NONE");
            map.put("group_id_list", "test");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = "24.7a69782ede6cc4bdfbd837b2324c47ec.2592000.1575279256.282335-17649816";

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result+"********************************");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String mulitFaceSearch(String imageBase64) {

        try {
            Map<String, Object> map = new HashMap<>();
            //map.put("image", "027d8308a2ec665acb1bdf63e513bcb9");
            map.put("image",imageBase64);
            map.put("liveness_control", "NONE");
            map.put("group_id_list", "test");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");
            map.put("max_face_num",5);
            map.put("max_user_num",5);

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = "24.7a69782ede6cc4bdfbd837b2324c47ec.2592000.1575279256.282335-17649816";

            String result = HttpUtil.post(url2, accessToken, "application/json", param);
            System.out.println(result+"********************************");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
       // FaceSearch.faceSearch();
        try {
            String s = Base64Util.encodeFromPath("C:\\Users\\Administrator\\Desktop\\faces\\5.JPG");
            String reslut = faceSearch(s);
            System.out.println("faceSearch :  "+reslut);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}