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
 * httpclient �����ࣨʹ��jar����httpclient-4.2.5.jar httpcore-4.2.4.jar��
 * @author shy
 *
 */
public class WebClient {
	
	private static HttpClient mHttpClient = null;
	static {
		mHttpClient=createHttpClient();//���̲߳�������		
	}
	/**
	 * ����HTTPCLIENT
	 * @return
	 */
	private static  HttpClient createHttpClient(){  
		 // �����������, HTTPЭ��İ汾,1.1/1.0/0.9   
	    HttpParams params = new BasicHttpParams();   
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);   
	    HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");   
	    HttpProtocolParams.setUseExpectContinue(params, true);
	    //HttpProtocolParams.setContentCharset(params, CHARSET);
	  
	    //�������ӳ�ʱʱ��   
	    int REQUEST_TIMEOUT = 10*1000;  //��������ʱ10����   
	    int SO_TIMEOUT = 10*1000;       //���õȴ����ݳ�ʱʱ��10����   
	    //HttpConnectionParams.setConnectionTimeout(params, REQUEST_TIMEOUT);  
	    //HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);  
	    params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, REQUEST_TIMEOUT);    
	    params.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);   
	    
	    //���÷���Э��   
	    SchemeRegistry schreg = new SchemeRegistry();    
	    schreg.register(new Scheme("http",80,PlainSocketFactory.getSocketFactory()));   
	    schreg.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));         
	      
	    //�����ӵ��̰߳�ȫ�Ĺ�����   
	    PoolingClientConnectionManager pccm = new PoolingClientConnectionManager(schreg);  
	    pccm.setDefaultMaxPerRoute(40); //ÿ�������������������   
	    pccm.setMaxTotal(200);          //�ͻ����ܲ������������      
	      
	    DefaultHttpClient httpClient = new DefaultHttpClient(pccm, params);  
       return httpClient;  
   }
	
	/**
	 * httpclient��post����post����Ϊһ���json�ַ��������̲߳����������ã��Ѳ��ԣ�
	 * ���ʱ��	2016-01-09
	 * ����ˣ�ʩ��Ԫ
	 * @param postData		JSON��ʽ���ַ���
	 * @param url			�����ַ	
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
	    * �ͷ�HttpClient���� 
	    *  
	    * @param hrb 
	    * ������� 
	    * @param httpclient 
	    *           client���� 
	    */  
	    public static void abortRequest(final HttpRequestBase hrb){  
	        if (hrb != null && hrb.isAborted()) {  
	            hrb.abort();  
	        }  
	    }
	    
		/**
		 * ģ�������post�ύ
		 * 
		 * @param url
		 * @return
		 */
		public static HttpPost getPostMethod(String url) {
			HttpPost pmethod = new HttpPost(url); // ������Ӧͷ��Ϣ
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
		 * ģ�������GET�ύ
		 * @param url
		 * @return
		 */
		public static HttpGet getGetMethod(String url) {
			HttpGet pmethod = new HttpGet(url);
			// ������Ӧͷ��Ϣ
			pmethod.addHeader("Connection", "keep-alive");
			pmethod.addHeader("Cache-Control", "max-age=0");
			pmethod.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
			pmethod.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/;q=0.8");
			return pmethod;
		}
	
}
