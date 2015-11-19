package com.ss.sssdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ljw on 15/11/12.
 */
public class SdkTools {
    public static boolean isPhone(String phone) {
        if(phone.length() != 11) return false;
        return true;
    }
    public static boolean isDeviceId(String id) {return true;}
    public static Context context = null;
    public static void setKV(String key,String value) {
        SharedPreferences sp = context.getSharedPreferences("setting", 0);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(key,value);
        ed.commit();
    }
    public static String getKV(String key) {
        SharedPreferences sp = context.getSharedPreferences("setting", 0);
        return sp.getString(key,"");
    }
    public static void clearKV() {
        SharedPreferences sp = context.getSharedPreferences("setting", 0);
        SharedPreferences.Editor ed = sp.edit();
        ed.clear();
        ed.commit();
    }
    public static String getCode(String str) {
        String regEx="\\d*";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            String n = m.group();
            int l = n.length();
            if (l>=4 && l<=6) {
                return n;
            }
        }
        return null;
    }
    public static void sendSMS(String phone,String txt) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null, txt, null, null);
    }
}
