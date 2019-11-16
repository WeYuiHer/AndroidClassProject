package com.example.testfromteacher;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;


public class detect_value {
    private JsonObject gender_list = new JsonObject();
    private JsonObject emotion_list = new JsonObject();
    private JsonObject expression_list= new JsonObject();
    public JsonObject gender_list_init(){
        try {
            gender_list.addProperty("male","男");
            gender_list.addProperty("female","女");
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return gender_list;
    }
    public JsonObject emotion_list_init(){
        try {
            emotion_list.addProperty("angry", "愤怒");
            emotion_list.addProperty("disgust", "厌恶");
            emotion_list.addProperty("fear", "恐惧");
            emotion_list.addProperty("happy", "高兴");
            emotion_list.addProperty("surprise", "惊讶");
            emotion_list.addProperty("neutral", "面无表情");
            emotion_list.addProperty("sad", "伤心");
            emotion_list.addProperty("pouty", "撅嘴");
            emotion_list.addProperty("grimace", "在做鬼脸");
        }catch (JsonParseException e) {
            e.printStackTrace();
        }
        return emotion_list;
    }
    public JsonObject expression_list_init(){
        try {
            expression_list.addProperty("none", "不笑");
            expression_list.addProperty("smile", "微笑");
            expression_list.addProperty("laugh", "大笑");
        }
        catch (JsonParseException e) {
            e.printStackTrace();
        }
        return expression_list;
    }
}
