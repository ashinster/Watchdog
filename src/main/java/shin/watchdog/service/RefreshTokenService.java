package shin.watchdog.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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

public class RefreshTokenService {

	final static Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

	public static String refreshToken;

	public RefreshTokenService(){

	}

	public static String refreshToken(){
		refreshToken = refreshTokenHelper(false);
		return refreshToken;
	}

	private static String refreshTokenHelper(boolean isRetry) {
		String token = null;

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
						
						token = jsonObject.get("access_token").getAsString();

						EntityUtils.consume(entity);
					}
				}
			} catch (IOException e) {
				logger.error("IO error refreshing token", e);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("Unsupported encoding exception", e);
		}
		
		if(token == null){
			if(isRetry){
				logger.error("Failed to refresh token");
			} else {
				logger.warn("Retrying getting access token");
				token = refreshTokenHelper(true);
			}
		}

		return token;
	}
}