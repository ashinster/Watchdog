package shin.watchdog.actions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import shin.watchdog.data.Site;
import shin.watchdog.main.Main;
import shin.watchdog.scheduled.RefreshTokenRunnable;

public class SendPrivateMessage {

	public static void sendPM(String subject, String body){
		sendPMHelper(subject, body, false);
	}

	private static boolean sendPMHelper(String subject, String content, boolean isRetry){

		boolean isSuccess = false;

		String token = RefreshTokenRunnable.refreshToken(false);

		if(token != null){
			String tokenURL = "https://oauth.reddit.com/api/compose";
			HttpPost httppost = new HttpPost(tokenURL);

			httppost.setHeader("Authorization", "Bearer " + token);
			httppost.setHeader("User-Agent", "WatchdogSA/0.1 by TimidSA");	

			ArrayList<String> sendToUsers = new ArrayList<>();
			sendToUsers.add("timidsa");
			// Can add more users

			// Can support sending to multiple users
			for(String user : sendToUsers){
				// Request parameters and other properties.
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("api_type", "json"));
				params.add(new BasicNameValuePair("subject", subject));
				params.add(new BasicNameValuePair("text", content));	
				params.add(new BasicNameValuePair("to", user));

				// Execute and get the response.
				HttpResponse response = null;
				try {
					httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

					System.out.println("Using access token: " + token);
					System.out.println("Sending PM...");

					response = Site.httpclient.execute(httppost);
					
					if(response != null){
						// Get timestamp of when PM was sent
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd EEE HH:mm:ss");
						Date resultdate = new Date(System.currentTimeMillis());

						System.out.println(response.getStatusLine().getStatusCode());
						if (response.getStatusLine().getStatusCode() >= 300) {
							System.out.println("Error sending PM: " + response.getStatusLine() + "\n");
						} else {
							isSuccess = true;
							System.out.println("PM sent at " + sdf.format(resultdate));
							System.out.println();
						}

						EntityUtils.consume(response.getEntity());

						return isSuccess;
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Refresh token was null, PM not sent.");
		}

		if(!isRetry){
			System.out.println("Retrying sending PM...");
			return sendPMHelper(subject, content, true);
		} else { 
			System.out.println("Send PM retry failed.");
			return isSuccess;
		}
	}
}
