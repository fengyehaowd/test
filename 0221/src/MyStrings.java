import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class MyStrings {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String getCharSet(String html)
	{
		String result = "utf-8";
		String[] ele1 = html.split("charset=\"{0,1}");
		if(ele1.length <=1)
			return result;
		else
		{
			String[] ele2 = ele1[1].split("\"");
			if(ele2.length<=1)
				return result;
			else
			{
				result = ele2[0];
				return result;
			}
		}
	}
	public static String getWWW(String url)
	{
		String[] ele = url.split("//");
		if(ele.length!=2)
			return url;
		else
		{
			
			String result = ele[1].replace("/", "");
			System.out.println(result);
			return result;
		}
	}
	
	public static String getTitle(String URL) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub

		//String URL ="http://www.baidu.com";
		System.out.println(URL);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet get = new HttpGet(URL);
		get.addHeader("Accept", "text/html, application/xhtml+xml,*/*");
		get.addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		get.addHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
		get.addHeader("Accept-Encoding", "gzip, deflate");
		get.addHeader("Connection", "Keep-Alive");
		get.addHeader("Host",getWWW(URL));
		CloseableHttpResponse response = httpClient.execute(get);
		HttpEntity responseEntity = response.getEntity();
		
		//Header h = responseEntity.getContentEncoding();
		//System.out.println(h.getValue());
		//System.out.println(EntityUtils.toString(responseEntity));
		byte[] b = EntityUtils.toByteArray(responseEntity);
		response.close();
		httpClient.close();
		String html = new String(b,"utf-8");
		String charSet = getCharSet(html);
		System.out.println(charSet);
		
		html = new String(b,charSet);
		
		Document doc = Jsoup.parse(html);
	
		Elements title = doc.select("title");
	
		String result = title.get(0).text();

		System.out.println(result);
		//JOptionPane.showConfirmDialog(, )
		return result;
	}

}
