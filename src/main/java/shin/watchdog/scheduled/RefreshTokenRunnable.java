package shin.watchdog.scheduled;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import shin.watchdog.data.Site;

public class RefreshTokenRunnable implements Runnable {

	private static BlockingQueue<String> queue;
	
	@Override
	public void run() {
		try{
			refreshToken(false);
		}catch(Throwable t) {
			System.out.println("Caught exception in ScheduledExecutorService. StackTrace:\n" + t.getStackTrace());
		}
	}
	
	public RefreshTokenRunnable(BlockingQueue<String> queue) {
		RefreshTokenRunnable.queue = queue;
	}

	public static String refreshToken(boolean isRetry) {

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
				response = Site.httpclient.execute(httppost);

				if (response.getStatusLine().getStatusCode() >= 300) {
					System.out.println("Error refreshing token: " + response.getStatusLine() + "\n");
				} else {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
					    
						JsonObject jsonObject = 
								new JsonParser().parse(EntityUtils.toString(entity)).getAsJsonObject();
						
						String token = jsonObject.get("access_token").getAsString();

						EntityUtils.consume(entity);

						return token;
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
		
		if(!isRetry){
			System.out.println("Retrying getting access token");
			return refreshToken(true);
		}

		System.out.println("Retry failed for getting access token");
		return null;
	}
}