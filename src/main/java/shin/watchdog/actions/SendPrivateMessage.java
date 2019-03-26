package shin.watchdog.actions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shin.watchdog.Watchdog;

public class SendPrivateMessage {
	
	final static Logger logger = LoggerFactory.getLogger(SendPrivateMessage.class);	

	public static boolean sendPM(String body, String webhookUrl){
		return sendPMHelper(body, webhookUrl, false);
	}

	private static boolean sendPMHelper(String content, String webhookUrl, boolean isRetry){
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

	/**
	 * Send the request to create the alert
	 * @param postRequest The request to execute
	 * @return
	 */
	private static boolean makeRequest(HttpPost postRequest){
		boolean isSuccess = false;

		// Execute and get the response.
		logger.info("Sending alert..");
		try(CloseableHttpResponse response = Watchdog.httpclient.execute(postRequest)) {			
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
		}

		return isSuccess;
	}
}
