
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.util.Elements;


import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.jsoup.nodes.*;

public class test_http {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("http://www.baidu.com");
		CloseableHttpResponse response1 = httpclient.execute(httpGet);
		// The underlying HTTP connection is still held by the response object
		// to allow the response content to be streamed directly from the network socket.
		// In order to ensure correct deallocation of system resources
		// the user MUST either fully consume the response content  or abort request
		// execution by calling CloseableHttpResponse#close().
		File output = new File("c:\\http.txt");
	    FileOutputStream file=new FileOutputStream(output);
	    BufferedWriter log = new BufferedWriter(new OutputStreamWriter(file));
	    log.write("http");
	    log.flush();
		try {
		   // System.out.println(response1.getStatusLine());
		    //System.out.println(response1.);
		  //  HttpEntity entity1 = response1.getEntity();
		    // do something useful with the response body
		    // and ensure it is fully consumed
		  //  EntityUtils.consume(entity1);
		   // System.out.println(EntityUtils.toString(entity1));
		   // entity1.writeTo(System.out);
		} finally {
		    response1.close();
		}   
		
		org.jsoup.nodes.Document doc = Jsoup.connect("http://www.sina.com").get();
		//System.out.println(doc.title());
		org.jsoup.select.Elements links=doc.select("a[href]");
		for(Element link:links)
			System.out.println(link.attr("abs:href"));
		
	/*	HttpPost httpPost = new HttpPost("http://www.baidu.com");
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("username", "vip"));
		nvps.add(new BasicNameValuePair("password", "secret"));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response2 = httpclient.execute(httpPost);

		try {
		    System.out.println(response2.getStatusLine());
		  //  HttpEntity entity2 = response2.getEntity();
		    // do something useful with the response body
		    // and ensure it is fully consumed
		   // EntityUtils.consume(entity2);
		} finally {
		    response2.close();
		}
*/
	}

}