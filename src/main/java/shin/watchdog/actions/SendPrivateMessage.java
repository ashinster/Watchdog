package shin.watchdog.actions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
import shin.watchdog.service.RefreshTokenService;

public class SendPrivateMessage {
	
	final static Logger logger = LoggerFactory.getLogger(SendPrivateMessage.class);	

	public boolean sendPM(String subject, String body, List<String> users){
		return sendPMHelper(subject, body, users, false);
	}

	private boolean sendPMHelper(String subject, String content, List<String> users, boolean isRetry){
		boolean allMessagesSent = true;

		String token = RefreshTokenService.refreshToken(false);

		if(token == null){
			logger.error("Refresh token was null, PM not sent. Sorry bud");
		} else {
			HttpPost httppost = new HttpPost("https://oauth.reddit.com/api/compose");

			httppost.setHeader("Authorization", "Bearer " + token);
			httppost.setHeader("User-Agent", "WatchdogSA/0.1 by TimidSA");	

			logger.info("Using access token: {}", token);
			logger.info("Subject: {}", subject);

			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("api_type", "json"));
			params.add(new BasicNameValuePair("subject", subject));
			params.add(new BasicNameValuePair("text", content));	

			for(String user : users){
				params.add(new BasicNameValuePair("to", user));
				
				httppost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

				// Make the call
				if(!makeRequest(httppost, user)){
					logger.warn("Retrying sending PM...");

					// Retry the request
					if(!makeRequest(httppost, user)){
						logger.error("Sending PM failed for user [{}] ;[", user);
						allMessagesSent = false;
					}
				}

				// Remove the 'to' param which is the last element so we can set the next user
				params.remove(params.size()-1);
			}
		}

		return allMessagesSent;
	}

	private boolean makeRequest(HttpPost postRequest, String user){
		boolean isSuccess = false;

		// Execute and get the response.
		HttpResponse response = null;
		HttpEntity entity = null;
		try {
			logger.info("Sending PM to: {}", user);
			response = Main.httpclient.execute(postRequest);
			entity = response.getEntity();
			
			if(response != null){
				logger.info("Status code: {}", response.getStatusLine().getStatusCode());

				if (response.getStatusLine().getStatusCode() >= 300) {
					logger.error("Error sending PM: {}", response.getStatusLine());
				} else {
					isSuccess = true;
					logger.info("PM successfully sent!");
					logger.info("Response: {}", EntityUtils.toString(response.getEntity()));
				}
			}
		} catch (IOException e) {
			logger.error("IO Error when attempting to send PM", e);
		} finally{
			if(entity != null){
				try {
					EntityUtils.consume(entity);
				} catch (IOException e) {
					logger.error("IO Error trying to consume entity", e);
				}
			}
		}

		return isSuccess;
	}
}
