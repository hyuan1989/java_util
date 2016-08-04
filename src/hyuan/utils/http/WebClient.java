package hyuan.utils.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

/**
 * httpclient 工具类（使用jar包：httpclient-4.2.5.jar httpcore-4.2.4.jar）
 * @author shy
 *
 */
public class WebClient {
	
	private static HttpClient mHttpClient = null;
	static {
		mHttpClient=createHttpClient();//多线程并发请求		
	}
	/**
	 * 创建HTTPCLIENT
	 * @return
	 */
	private static  HttpClient createHttpClient(){  
		 // 设置组件参数, HTTP协议的版本,1.1/1.0/0.9   
	    HttpParams params = new BasicHttpParams();   
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);   
	    HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");   
	    HttpProtocolParams.setUseExpectContinue(params, true);
	    //HttpProtocolParams.setContentCharset(params, CHARSET);
	  
	    //设置连接超时时间   
	    int REQUEST_TIMEOUT = 10*1000;  //设置请求超时10秒钟   
	    int SO_TIMEOUT = 10*1000;       //设置等待数据超时时间10秒钟   
	    //HttpConnectionParams.setConnectionTimeout(params, REQUEST_TIMEOUT);  
	    //HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);  
	    params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, REQUEST_TIMEOUT);    
	    params.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);   
	    
	    //设置访问协议   
	    SchemeRegistry schreg = new SchemeRegistry();    
	    schreg.register(new Scheme("http",80,PlainSocketFactory.getSocketFactory()));   
	    schreg.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));         
	      
	    //多连接的线程安全的管理器   
	    PoolingClientConnectionManager pccm = new PoolingClientConnectionManager(schreg);  
	    pccm.setDefaultMaxPerRoute(40); //每个主机的最大并行链接数   
	    pccm.setMaxTotal(200);          //客户端总并行链接最大数      
	      
	    DefaultHttpClient httpClient = new DefaultHttpClient(pccm, params);  
       return httpClient;  
   }
	
	/**
	 * httpclient的post请求（post数据为一般的json字符串）多线程并发请求适用（已测试）
	 * 添加时间	2016-01-09
	 * 添加人：施浩元
	 * @param postData		JSON格式的字符串
	 * @param url			请求地址	
	 * @return
	 * @throws Exception
	 */
	public  static String httpRequestPost(String postData, String url)
			throws Exception {
		HttpPost httpost = getPostMethod(url);
		String jsonStr="";
		HttpResponse response =null;
		try{		
		httpost.setEntity(new StringEntity(postData, "UTF-8"));
		//HttpResponse response = httpclient.execute(httpost);
		response =mHttpClient.execute(httpost);
		jsonStr = EntityUtils.toString(response.getEntity(), "utf-8");
		}catch (Exception e) {
			throw e;
		}finally{
			try {  
                if (response != null) {  
                    response.getEntity().getContent().close();  
                }
                abortRequest(httpost);
            } catch (IllegalStateException e) {  
                e.printStackTrace();  
            } catch (IOException e) {  
            	e.printStackTrace();  
            }
			
		}
		
		return jsonStr;
	}
	
	 /** 
	    * 释放HttpClient连接 
	    *  
	    * @param hrb 
	    * 请求对象 
	    * @param httpclient 
	    *           client对象 
	    */  
	    public static void abortRequest(final HttpRequestBase hrb){  
	        if (hrb != null && hrb.isAborted()) {  
	            hrb.abort();  
	        }  
	    }
	    
		/**
		 * 模拟浏览器post提交
		 * 
		 * @param url
		 * @return
		 */
		public static HttpPost getPostMethod(String url) {
			HttpPost pmethod = new HttpPost(url); // 设置响应头信息
			pmethod.addHeader("Connection", "keep-alive");
			pmethod.addHeader("Accept", "*/*");
			pmethod.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			pmethod.addHeader("Host", "mp.weixin.qq.com");
			pmethod.addHeader("X-Requested-With", "XMLHttpRequest");
			pmethod.addHeader("Cache-Control", "max-age=0");
			pmethod.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
			return pmethod;
		}

		/**
		 * 模拟浏览器GET提交
		 * @param url
		 * @return
		 */
		public static HttpGet getGetMethod(String url) {
			HttpGet pmethod = new HttpGet(url);
			// 设置响应头信息
			pmethod.addHeader("Connection", "keep-alive");
			pmethod.addHeader("Cache-Control", "max-age=0");
			pmethod.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
			pmethod.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/;q=0.8");
			return pmethod;
		}
	
}
