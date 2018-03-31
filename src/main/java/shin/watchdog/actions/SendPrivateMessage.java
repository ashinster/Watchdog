package shin.watchdog.actions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class SendPrivateMessage {
	String tokenURL = "https://oauth.reddit.com/api/compose";
	HttpClient httpclient;

	public void sendPM(String accessToken, String content) {

		httpclient = HttpClients.createDefault();

		tokenURL = "https://oauth.reddit.com/api/compose";
		HttpPost httppost = new HttpPost(tokenURL);

		System.out.println("Using access token: " + accessToken);
		httppost.setHeader("Authorization", "Bearer " + accessToken);
		httppost.setHeader("User-Agent", "WatchdogSA/0.1 by TimidSA");

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("api_type", "json"));
		params.add(new BasicNameValuePair("subject", "Potential Novatouch Listing Found!"));
		params.add(new BasicNameValuePair("text", content));
		params.add(new BasicNameValuePair("to", "TimidSA"));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Execute and get the response.
		HttpResponse response;
		try {
			System.out.println("Sending PM...");
			response = httpclient.execute(httppost);

			// Get timestamp of when PM was sent
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd EEE HH:mm:ss");
			Date resultdate = new Date(System.currentTimeMillis());

			System.out.println(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() >= 300) {
				System.out.println("Error sending PM: " + response.getStatusLine() + "\n");
			} else {
				System.out.println("PM sent at " + sdf.format(resultdate) + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
