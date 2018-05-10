package shin.watchdog.actions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import shin.watchdog.main.Main;
import shin.watchdog.scheduled.FetchPostRunnable;

public class SendPrivateMessage {

	public static void sendPM(String subject, String body){
		sendPMHelper(subject, body);
	}

	private static void sendPMHelper(String subject, String content){
		String tokenURL = "https://oauth.reddit.com/api/compose";
		HttpPost httppost = new HttpPost(tokenURL);

		httppost.setHeader("Authorization", "Bearer " + FetchPostRunnable.accessToken);
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

				System.out.println("Using access token: " + FetchPostRunnable.accessToken);
				System.out.println("Sending PM...");
				response = Main.httpclient.execute(httppost);

				// Get timestamp of when PM was sent
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd EEE HH:mm:ss");
				Date resultdate = new Date(System.currentTimeMillis());

				System.out.println(response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() >= 300) {
					System.out.println("Error sending PM: " + response.getStatusLine() + "\n");
				} else {
					System.out.println("PM sent at " + sdf.format(resultdate));
					System.out.println();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
