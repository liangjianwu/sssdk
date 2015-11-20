package ljw.tongcheng.main;

import java.util.HashMap;

public class MainThread extends Thread{
	private int maxthread = 1;
	public MainThread() {
	}
	public void run() {
		
		String smstoken = "";
		String userlogin = HttpRequest.sendGet("http://api.yma0.com/http.aspx",null,null, "action=loginIn&uid=zerosun&pwd=shanda");
		if(userlogin.length() == 24) {
			smstoken = userlogin.substring(8);
		}else {
			System.out.println("login sms platform failed:" + userlogin);
			return;
		}
		int maxcount = 10;
		while(maxcount > 0 ) {	
			while(TaskThread.tasklist.size()>=maxthread) {
				try {
					sleep(3000);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			String getphone = HttpRequest.sendGet("http://api.yma0.com/http.aspx",null,null, "action=getMobilenum&pid=10073&uid=zerosun&token="+smstoken+"&size=1");
			if(getphone.length() == 28) {
				String phone = getphone.substring(0,11);
				if(!isNumeric(phone)) {
					System.out.println("phone is not phone:"+phone);
					continue;
				}				
				TaskThread tt = new TaskThread(smstoken,phone);
				tt.start();					
				maxcount--;
			}else {
				System.out.println("getphone failed:" + getphone);
			}			
		}
		while(TaskThread.tasklist.size() > 0) {
			try {
				sleep(3000);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("main thread exit");
	}
	public boolean isNumeric(String str){
	  for (int i = str.length();--i>=0;){  
	   if (!Character.isDigit(str.charAt(i))){
	    return false;
	   }
	  }
	  return true;
	}
}
