package com.ss.sssdk;

import org.json.JSONObject;

/**
 * Created by ljw on 15/11/12.
 */
public class JsonObject extends JSONObject {
    public Object Get(String key) {
        if(this.has(key)) {
            try {
                return this.get(key);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
