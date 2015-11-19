package ljw.tongcheng.main;

import java.util.HashMap;

public class TongchengReg {
	
	public static void main(String[] args) {
		MainThread mt = new MainThread();
		mt.start();
		try {
			mt.join();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}


//String s=HttpRequest.sendGet("http://www.sina.com.cn", "key=123&v=456");
//System.out.println(s);

//发送 POST 请求
//String[] phones = {"13790315137",
//		"13790303852",
//		"13790096949",
//		"13790096910",
//		"13790096869",
//		"13790064923",
//		"13790064757",
//		"13790037363",
//		"13790030730",
//		"13790012204",
//		"13782577594",
//		"13782534461",
//		"13782533144",
//		"13782527443",
//		"13782525447",
//		"13782524949",
//		"13782524747"};
//for(int i=0;i<phones.length;i++) {
//	long time = System.currentTimeMillis();
//	String phone = phones[i];
//	String servicename = "getverificationcoderegister";
//	String[] originalArray={"Version=20111128102912","AccountID=c26b007f-c89e-431a-b8cc-493becbdd8a2", "ServiceName="+servicename, "ReqTime=1446527824734"};
//	String[] sortedArray = SignHeader.BubbleSort(originalArray);
//	String sign = SignHeader.GetMD5ByArray(sortedArray, "8874d8a8b8b391fbbd1a25bda6ecda11", "utf-8");
//	String para = "{\"request\":{\"body\":{\"clientInfo\":{\"clientIp\":\"192.168.2.87\",\"deviceId\":\"a21e4c37c5d1ecd9\",\"extend\":\"4^5.0.2,5^PLK-AL10,6^-1\",\"mac\":\"24:1f:a0:c1:d1:b1\",\"manufacturer\":\"HUAWEI\",\"networkType\":\"wifi\",\"pushInfo\":\"68f6b15099d28bd943e1b28ec5d21d646cf383fe\",\"refId\":\"42931004\",\"versionNumber\":\"7.5.3\",\"versionType\":\"android\"},\"mobile\":\""+phone+"\"},\"header\":{\"accountID\":\"c26b007f-c89e-431a-b8cc-493becbdd8a2\",\"digitalSign\":\""+sign+"\",\"reqTime\":\""+time+"\",\"serviceName\":\""+servicename+"\",\"version\":\"20111128102912\"}}}";
//    String reqdata = SignHeader.ReqdataMd5(para+"4957CA66-37C3-46CB-B26D-E3D9DCB51535");
//    //"71b47d21dc94c8688a2087bc6bac16f7"
//	String sr=BackHttpRequest.sendPost("http://tcmobileapi.17usoft.com/member/membershiphandler.ashx",reqdata, para);
//    System.out.println(phone+":  "+sr);
//}