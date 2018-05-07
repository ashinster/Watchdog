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

import shin.watchdog.data.Post;
import shin.watchdog.scheduled.FetchPostRunnable;

public class SendPrivateMessage {
	String tokenURL = "https://oauth.reddit.com/api/compose";
	HttpClient httpclient = HttpClients.createDefault();

	public String sendPM(String accessToken, String subject, Post post, ArrayList<String> matchedTerms, boolean isDebug) {
		String postId = post.name;
		String author = post.author;
		String description = post.selftext;
		String title = post.title;

		tokenURL = "https://oauth.reddit.com/api/compose";
		HttpPost httppost = new HttpPost(tokenURL);

		httppost.setHeader("Authorization", "Bearer " + accessToken);
		httppost.setHeader("User-Agent", "WatchdogSA/0.1 by TimidSA");		

		String pmHyperlink = "";
		for(String matchedTerm : matchedTerms){
			if(matchedTerm.equals("Panda")){
				matchedTerm += "s";
			}
			String pmBody = "Hey!%20I%27ll%20buy%20your%20" + matchedTerm + "%20if%20it%27s%20still%20available!%20Let%20me%20know%2C%20thanks!";

			pmHyperlink += String.format(
				"[Send PM for the %s](https://www.reddit.com/message/compose?to=%s&subject=%s&message=%s)", matchedTerm, author, matchedTerm, pmBody) + "\n\n";
		}

		String commentsHyperlink = String.format("[Leave a comment](https://www.reddit.com/r/mechmarket/comments/%s)", postId.substring(3));

		String content = String.format(
			"\"%s\"\n/u/%s\n\n\\---- Start Post ----/\n\n%s\n\n\\---- End Post ----/\n\n%s%s", title, author, description, pmHyperlink, commentsHyperlink);

		if(!isDebug){
			sendPM(httppost, subject, content);
		}

		return content;
	}

	private void sendPM(HttpPost httppost, String subject, String content){

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
				response = httpclient.execute(httppost);

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

	public void sendPMforGH(String subject, String body){
		tokenURL = "https://oauth.reddit.com/api/compose";
		HttpPost httppost = new HttpPost(tokenURL);
		
		httppost.setHeader("Authorization", "Bearer " + FetchPostRunnable.accessToken);
		httppost.setHeader("User-Agent", "WatchdogSA/0.1 by TimidSA");	

		sendPM(httppost, subject, body);

	}
}
