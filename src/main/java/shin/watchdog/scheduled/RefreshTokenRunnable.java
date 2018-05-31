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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shin.watchdog.main.Main;

public class RefreshTokenRunnable implements Runnable {

	final static Logger logger = LoggerFactory.getLogger(RefreshTokenRunnable.class);
	
	@Override
	public void run() {
		try{
			refreshToken(false);
		}catch(Throwable t) {
			logger.error("Caught an unknown exception while retrieving refresh token", t);
		}
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
				response = Main.httpclient.execute(httppost);

				if (response.getStatusLine().getStatusCode() >= 300) {
					logger.error("Error refreshing token {}", response.getStatusLine());
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
				logger.error("IO error refreshing token", e);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("Unsupported encoding exception", e);
		}
		
		if(!isRetry){
			logger.warn("Retrying getting access token");
			return refreshToken(true);
		}

		logger.error("Retry failed for getting access token");
		return null;
	}
}