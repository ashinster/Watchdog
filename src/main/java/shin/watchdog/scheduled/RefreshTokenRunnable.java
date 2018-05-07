package shin.watchdog.scheduled;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RefreshTokenRunnable implements Runnable {

	private final BlockingQueue<String> queue;
	private HttpClient httpclient = HttpClients.createDefault();
	
	@Override
	public void run() {
		try{
			refreshToken();
		}catch(Throwable t) {
			System.out.println("Caught exception in ScheduledExecutorService. StackTrace:\n" + t.getStackTrace());
		}
	}
	
	public RefreshTokenRunnable(BlockingQueue<String> queue) {
		this.queue = queue;
	}

	private void refreshToken() {

		String tokenURL = "https://www.reddit.com/api/v1/access_token";
		HttpPost httppost = new HttpPost(tokenURL);

		try {
			String encoding = Base64.getEncoder()
					.encodeToString("94cDSI3XNGAIUg:WxgR_vIVyzwUzSLFg6PAgcKTddI".getBytes("utf-8"));
			httppost.setHeader("Authorization", "Basic " + encoding);
			httppost.setHeader("User-Agent", "WatchdogSA/0.1 by TimidSA");

			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("grant_type", "refresh_token"));
			params.add(new BasicNameValuePair("refresh_token", "38880069594-L5JgQMC5Mb7BxpMwWu01aCCnP_g"));

			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			// Execute and get the response.
			HttpResponse response;
			try {
				//System.out.println("Refreshing access token ");
				response = httpclient.execute(httppost);

				if (response.getStatusLine().getStatusCode() >= 300) {
					System.out.println("Error refreshing token: " + response.getStatusLine() + "\n");
				} else {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
					    
						JsonObject jsonObject = 
								new JsonParser().parse(EntityUtils.toString(entity)).getAsJsonObject();
						
						String token = jsonObject.get("access_token").getAsString();
						//System.out.println("New token is: " + token);
						
						// Put the found token in the queue for the consumer to set the access token
						try {
							queue.put(token);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd EEE HH:mm:ss");
			    Date resultdate = new Date(System.currentTimeMillis());
				System.out.println(sdf.format(resultdate) + " - Error refreshing token: " + e.getMessage());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}