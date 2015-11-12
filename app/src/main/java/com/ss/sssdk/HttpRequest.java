package com.ss.sssdk;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public interface IHttpCallBack {
        public void callback(String ret);
        public void callback(JSONObject ret);
    }

    public static void sendGet(final String url,final String server_name,final JSONObject header,final String param, final IHttpCallBack callback) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                String ret = HttpRequest.sendGet(url, server_name,header,param);
                callback.callback(ret);
            }
        };
        thread.start();
    }
    public static void sendPost(final String url,final String server_name,final JSONObject header,final String param, final IHttpCallBack callback) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                String ret = HttpRequest.sendPost(url,server_name, header,param);
                callback.callback(ret);
            }
        };
        thread.start();
    }

    public static void sendGetJSONObject(final String url,final String server_name,final JSONObject header,final String param, final IHttpCallBack callback) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                JSONObject ret = HttpRequest.sendGetJSONObject(url, server_name,header, param);
                callback.callback(ret);
            }
        };
        thread.start();
    }
    public static void sendPostJSONObject(final String url,final String server_name,final JSONObject header,final String param, final IHttpCallBack callback) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                JSONObject ret = HttpRequest.sendPostJSONObject(url, server_name,header, param);
                callback.callback(ret);
            }
        };
        thread.start();
    }
    public static JSONObject sendGetJSONObject(String url,String server_name,JSONObject header, String param) {
        String result = sendGet(url, server_name,header, param);
        try {
            JSONObject ret = new JSONObject(result);
            return ret;
        }catch(Exception e) {
            return null;
        }
    }
    public static JSONObject sendPostJSONObject(String url,String server_name,JSONObject header, String param) {
        String result = sendPost(url, server_name,header, param);
        try {
            JSONObject ret = new JSONObject(result);
            return ret;
        }catch(Exception e) {
            return null;
        }
    }
    public static String sendGet(String url,String server_name,JSONObject header, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            if(server_name != null)
                conn.setRequestProperty("SERVER_NAME", server_name);
            if(header != null) {
                Iterator<?> it = header.keys();
                while (it.hasNext()) {//遍历JSONObject
                    String key = (String) it.next().toString();
                    String value = header.getString(key);
                    conn.setRequestProperty(key, value);
                }
            }else {
                conn.setRequestProperty("accept", "*/*");
                conn.setRequestProperty("user-agent","QQ&WEIXIN");
                conn.setRequestProperty("Content-Type", "application/text");
                conn.setRequestProperty("connection", "Keep-Alive");
            }
            // 建立实际的连接
            conn.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = conn.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    /*
     * Content-Type: application/json
		Content-Length: 530
		Host: tcmobileapi.17usoft.com
		Connection: Keep-Alive
		Accept-Encoding: gzip
		secver: 4
		reqdata: ec8c20d4169230cfb1920afd2d22efd0
		sxx: 4c16245713da9440f3da87c0e95f78a1
     * 
     * 
     */
    public static String sendPost(String url,String server_name,JSONObject header, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            if(server_name != null)
                conn.setRequestProperty("SERVER_NAME", server_name);
            //conn.setRequestProperty("accept", "*/*");
//            conn.setRequestProperty("connection", "Keep-Alive");
//            conn.setRequestProperty("user-agent","okhttp/2.2.0-SNAPSHOT");
//            conn.setRequestProperty("Host","tcmobileapi.17usoft.com");
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setRequestProperty("Accept-Encoding","gzip");
//            conn.setRequestProperty("secver","4");
//            conn.setRequestProperty("reqdata",reqdata);//"71b47d21dc94c8688a2087bc6bac16f7");
//            conn.setRequestProperty("sxx","d99bb670f5c62fd149c107e590580af5");
            if(header != null) {
                Iterator<?> it = header.keys();
                while (it.hasNext()) {//遍历JSONObject
                    String key = (String) it.next().toString();
                    String value = header.getString(key);
                    conn.setRequestProperty(key, value);
                }
            }else {
                conn.setRequestProperty("accept", "*/*");
                conn.setRequestProperty("user-agent","QQ&WEIXIN");
                conn.setRequestProperty("Content-Type", "application/text");
                conn.setRequestProperty("connection", "Keep-Alive");
            }
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    
}