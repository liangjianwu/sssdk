package ljw.tongcheng.main;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

public class TaskThread extends Thread{
	public static HashMap<String,TaskThread> tasklist = new HashMap<String,TaskThread>();
    public int status = 0; 
    private String token = "";
    private String smstoken = "";
    private String phone = null;
    public String threadid = "";
    private String proxyip = null;
    private String proxyport = null;
    public TaskThread(String _proxyip,String _proxyport,String _smstoken) {        
        smstoken = _smstoken;
        proxyip = _proxyip;
        proxyport = _proxyport;
        threadid = "Task"+System.currentTimeMillis();
        tasklist.put(threadid, this);
    }
    private int rand(int b,int e) {
    	int r = ((int)Math.floor(Math.random()*100000))%(e-b);
    	return b+r;
    }
    private String numString(int len) {
    	String ret ="";
    	for(int i=0;i<len;i++) {
    		ret +=rand(0,10);
    	}
    	return ret;
    }
    private String newmac() {
    	String[] chr = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
    	return chr[rand(0,16)]+chr[rand(0,16)]+":"+chr[rand(0,16)]+chr[rand(0,16)]+":"+chr[rand(0,16)]+chr[rand(0,16)]+":"+chr[rand(0,16)]+chr[rand(0,16)]+":"+chr[rand(0,16)]+chr[rand(0,16)]+":"+chr[rand(0,16)]+chr[rand(0,16)];
    }
    private String randOperator() {
    	String[] cc = {"China Telecom","CHINA MOBILE","CMCC","China Unicom","中国联通","中国电信","中国移动"};
    	return cc[rand(0,cc.length)];
    	
    }
    private String getVersion(int idx) {
    	String[] cc = {"5.0.2","5.1.1","4.3","4.1","5.1.2"};
    	return cc[idx];
    	
    }
    private String getModel(int idx) {
    	String[] cc = {"PLK-AL10","SM-G9250","GT-I9500","MI NOTE Pro","MI-4"};
    	return cc[idx];
    	
    }
    private String getDevice(int idx) {
    	String[] cc = {"HWPLK","zeroltechn","ja3g","leo","leo"};
    	return cc[idx];    	
    }
    private String getProduct(int idx) {
    	String[] cc = {"PLK-AL10","zeroltezc","ja3gzn","leo","leo"};
    	return cc[idx];    	
    }
    private String getManufacturer(int idx) {
    	String[] cc = {"HUAWEI","samsung","samsung","Xiaomi","Xiaomi"};
    	return cc[idx];    	
    }
    private String getSDK(int idx) {
    	String[] cc = {"21","22","18","21","21"};
    	return cc[idx];    	
    }
    private String newdevice(String phone) {
    	JSONObject device = new JSONObject();
    	device.put("mac",newmac());
        device.put("Imei", numString(15)); //359596061860312
        device.put("SoftVersion","00");
        device.put("Phone",phone);
        device.put("Operator",randOperator());
        device.put("PhoneType",rand(1,3));
        device.put("SimSerial", numString(20)); //89860019101550113037
        
        int idx = rand(0,5);
        device.put("VersionRelease", getVersion(idx));
        device.put("Model",getModel(idx));
        device.put("Device",getDevice(idx));
        device.put("Product",getProduct(idx));
        device.put("SDK",getSDK(idx));
        device.put("Manufacturer",getManufacturer(idx));
        device.put("OsID","");
        device.put("User", "");
    	String deviceinfo = device.toString();
    	JSONObject ret = HttpRequest.sendPostJSONObject(proxyip,proxyport,HttpUrl.monitor_url, HttpUrl.REPORT_DEVICE_INFO,null, "d="+deviceinfo);    	
        try {
            if (ret != null && ret.getBoolean("b")) {
            	return ret.getString("result");
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean isNumeric(String str){
  	  for (int i = str.length();--i>=0;){  
  	   if (!Character.isDigit(str.charAt(i))){
  	    return false;
  	   }
  	  }
  	  return true;
  	}
    @Override
    public void run() {  
    	System.out.println(this.threadid + "start");
    	String getphone = HttpRequest.sendGet(null,null,"http://api.yma0.com/http.aspx",null,null, "action=getMobilenum&pid=10073&uid=zerosun&token="+smstoken+"&size=1");
		//String getphone = "18819045294|1111111111111111";
    	//String phone = null;
    	if(getphone.length() == 28) {
			phone = getphone.substring(0,11);
			if(!isNumeric(phone)) {				
				System.out.println(this.threadid + "end with error phone "+ phone);
				tasklist.remove(this.threadid);
				return;
			}
		}else {
			System.out.println(this.threadid + "end with not phone ");
			tasklist.remove(this.threadid);
			return;
		}
    	System.out.println(System.currentTimeMillis()+":" + phone+" start");
    	token = newdevice(phone); 
    	JSONObject header = new JSONObject();
        String ret = HttpRequest.sendGet(proxyip,proxyport,HttpUrl.monitor_url,HttpUrl.HEART_BEAT,header,"token="+token);
        try {
            JSONObject result = JSONObject.fromObject(ret);
            if(result.getBoolean("b")) {
                if(result.has("result")) {
                    JSONObject task = result.getJSONObject("result");
                    excuteTask(task);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis()+":" + phone+" end");
        tasklist.remove(this.threadid);
    }
    private String getCode(String str) {
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
    private void excuteTask(JSONObject task) {        
        try {        	
            if(!task.has("taskid")) return;
            sleep(2000);
            int taskid = task.getInt("taskid");
            int step = task.getInt("step");
            if(task.has("wait_sms")) {
            	String smscode = null;
                for(int i=0;i<300;i++) {
                	String ret = HttpRequest.sendGet(null,null,"http://api.yma0.com/http.aspx",null,null, "action=getVcodeAndReleaseMobile&uid=zerosun&token="+smstoken+"&pid=10073&mobile="+phone);
                	if(ret != null && !ret.equalsIgnoreCase("") && ret.length()>12) {
                		//18820237082|7f3cbaad27dc441c
                		String body = ret.substring(12);  
                		if(body != null && body.length()>0) {
                			smscode = getCode(body);
                			break;
                		}
                	}else {
                		sleep(2000);
                	}
                }
                if(smscode != null && smscode.length() == 6) {
                    JSONObject ret = HttpRequest.sendPostJSONObject(proxyip,proxyport,HttpUrl.monitor_url,HttpUrl.REPORT_RESULT,null,"token="+token+"&id="+taskid+"&step="+step+"&result="+smscode);                        
                    if(ret != null && ret.getBoolean("b") && ret.has("result")) {
                        excuteTask(ret.getJSONObject("result"));
                    }
                }
            }else {
                String url = task.getString("url");
                String params = task.getString("params");
                JSONObject header = task.getJSONObject("header");
                String method = task.getString("method");
                String result = method.equalsIgnoreCase("POST")?HttpRequest.sendPost(proxyip,proxyport,url, null, header, params):HttpRequest.sendGet(proxyip,proxyport,url, null, header, params);
                JSONObject ret = HttpRequest.sendPostJSONObject(proxyip,proxyport,HttpUrl.monitor_url, HttpUrl.REPORT_RESULT, null, "token="+token+"&id=" + taskid + "&step=" + step + "&result="+result);
                if(ret != null && ret.getBoolean("b") && ret.has("result")) {
                    excuteTask(ret.getJSONObject("result"));
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}

