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

public class SendPrivateMessage {
	
	final static Logger logger = LoggerFactory.getLogger(SendPrivateMessage.class);	

	public static boolean sendPM(String subject, String body, String webhookUrl){
		return sendPMHelper(subject, body, webhookUrl, false);
	}

	private static boolean sendPMHelper(String subject, String content, String webhookUrl, boolean isRetry){
		boolean allMessagesSent = true;

		HttpPost httppost = new HttpPost(webhookUrl);

		logger.info("Sending request to: {}", webhookUrl);

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("content", content));
		
		httppost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

		// Make the call
		if(!makeRequest(httppost)){
			logger.warn("Retrying sending alert...");

			// Retry the request
			if(!makeRequest(httppost)){
				logger.error("Retry sending alert failed.");
			}
		}

		return allMessagesSent;
	}

	private static boolean makeRequest(HttpPost postRequest){
		boolean isSuccess = false;

		// Execute and get the response.
		HttpResponse response = null;
		HttpEntity entity = null;
		try {
			logger.info("Sending alert..");
			response = Main.httpclient.execute(postRequest);
			entity = response.getEntity();
			
			if(response != null){
				logger.info("Status code: {}", response.getStatusLine().getStatusCode());

				if (response.getStatusLine().getStatusCode() >= 300) {
					logger.error("Error sending PM: {}", response.getStatusLine());
				} else {
					isSuccess = true;
					logger.info("Alert successfully sent!");
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
