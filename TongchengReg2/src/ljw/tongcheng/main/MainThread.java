package ljw.tongcheng.main;

import java.util.HashMap;

public class MainThread extends Thread{
	private int maxthread = 30;
	
	public MainThread() {
	}
	public void run() {
		
		String smstoken = "";
		String userlogin = HttpRequest.sendGet(null,null,"http://api.yma0.com/http.aspx",null,null, "action=loginIn&uid=zerosun&pwd=shanda");
		if(userlogin.length() == 24) {
			smstoken = userlogin.substring(8);
		}else {
			System.out.println("login sms platform failed:" + userlogin);
			return;
		}
		int maxcount = 3;
		while(maxcount > 0 ) {	
			while(TaskThread.tasklist.size()>=maxthread) {
				try {
					sleep(1000);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}			
			TaskThread tt = new TaskThread("123.59.56.228","3456",smstoken);
			tt.start();					
			maxcount--;
			
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
