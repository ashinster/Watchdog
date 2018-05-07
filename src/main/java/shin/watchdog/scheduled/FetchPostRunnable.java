package shin.watchdog.scheduled;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import shin.watchdog.actions.SendPrivateMessage;
import shin.watchdog.data.Post;
import shin.watchdog.data.Posts;

public class FetchPostRunnable implements Runnable {

	public static String accessToken;

	private SendPrivateMessage sendPM = new SendPrivateMessage();

	HttpClient httpclient = HttpClients.createDefault();

	private Posts postsCache = new Posts();

	private ArrayList<String> previousList = new ArrayList<>();
	
	ArrayList<String> matchedTerms = new ArrayList<>();

	boolean isDebug;

	Map<String, ArrayList<String>> searchItems;

	public FetchPostRunnable(Map<String, ArrayList<String>> searchItems) {
		this.searchItems = searchItems;
		this.isDebug = false;
	}

	public FetchPostRunnable(Map<String, ArrayList<String>> searchItems, boolean isDebug) {
		this.searchItems = searchItems;
		this.isDebug = isDebug;
	}

	@Override
	public void run() {
		try {
			fetchPostsHelper2();
		} catch (Throwable t) {
			System.out.println("Caught exception in FetchPostTask. StackTrace:");
			t.printStackTrace();
		}
	}

	//	private void fetchPostsHelper() {
	//		// Make the call to get the newest posts
	//		JsonObject jsonObject = makeCall();
	//
	//		if (jsonObject != null) {
	//			JsonArray newPostsArray = jsonObject.getAsJsonObject("data").getAsJsonArray("children");
	//			
	//			String tmp = "";
	//			String tmp2 = "";
	//			for (int i = 0; i < newPostsArray.size(); i++) {
	//				JsonObject aPostDataJson = newPostsArray.get(i).getAsJsonObject().getAsJsonObject("data");
	//				String postName = aPostDataJson.get("name").getAsString();
	//
	//				if (i == 0) {
	//					tmp = postName;
	//					// System.out.println("Base post: " + currentNewestPost + "\n");
	//				}else if(i == 1) {
	//					tmp2 = postName;
	//				}
	//				
	//				if (postName.equals(secondCurrentNewest) || postName.equals(currentNewestPost)) {
	//					break;
	//				} else {
	//					System.out.print("! ");
	//					parsePost(aPostDataJson, false);
	//				}
	//			}
	//			if (!tmp.isEmpty()) {
	//				currentNewestPost = tmp;
	//			}
	//			if(!tmp2.isEmpty()) {
	//				secondCurrentNewest = tmp2;
	//			}
	//		}
	//	}

	private void fetchPostsHelper2() {
		// Make the call to get the newest posts
		JsonObject jsonObject = makeCall();

		if (jsonObject != null) {
			JsonArray postsArray = jsonObject.getAsJsonObject("data").getAsJsonArray("children");

			Posts newPosts = populatePostsData(postsArray);
			ArrayList<String> newPostNames = newPosts.getPostNames();
			Map<String, JsonObject> newPostsMap = newPosts.getPostsData();

			ArrayList<String> tmp = newPosts.getNamesAsNewList();

			newPostNames.removeAll(previousList);

			//System.out.print(Arrays.toString(newPostNames.toArray()) + " ");

			if (!newPostNames.isEmpty()) {
				for (String s : newPostNames) {
					parsePost(newPostsMap.get(s));
					matchedTerms.clear();
				}
			}
			previousList = tmp;
		}
	}

	private Posts populatePostsData(JsonArray postsArray) {
		Posts posts = new Posts();

		ArrayList<String> tmp = new ArrayList<>();
		Map<String, JsonObject> data = new HashMap<>();

		for (int i = 0; i < postsArray.size(); i++) {
			JsonObject aPostDataJson = postsArray.get(i).getAsJsonObject().getAsJsonObject("data");
			String postName = aPostDataJson.get("name").getAsString();

			data.put(postName, aPostDataJson);
			tmp.add(postName);
		}

		posts.setPostNames(tmp);
		posts.setPostsData(data);
		return posts;
	}

	public JsonObject makeCall() {
		JsonObject jsonObject = null;

		String tokenURL = "https://www.reddit.com/r/mechmarket/new.json?restrict_sr=on&sort=new&t=all";
		// System.out.println(tokenURL);

		HttpGet httpget = new HttpGet(tokenURL);

		httpget.setHeader("User-Agent", "WatchdogSA/0.1 by TimidSA");

		// Get current time
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd EEE HH:mm:ss");
		Date resultdate = new Date(System.currentTimeMillis());

		// Execute and get the response.
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);

			if (response.getStatusLine().getStatusCode() >= 300) {
				System.out.println(
						"\n" + sdf.format(resultdate) + " - Error from response: " + response.getStatusLine() + "\n");
			} else {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					//System.out.print("... ");
					jsonObject = new JsonParser().parse(EntityUtils.toString(entity)).getAsJsonObject();
				}
			}
		} catch (Throwable e) {
			System.out.println("\n" + sdf.format(resultdate) + " - Error getting new posts: " + e.getMessage());
		}
		return jsonObject;
	}

	private String parsePost(JsonObject postData) {
		Post post = new Post();
		String postName = post.name = postData.get("name").getAsString();
		String title = post.title = postData.get("title").getAsString();
		String description = post.selftext = postData.get("selftext").getAsString();
		// String author = post.author = postData.get("author").getAsString();

		if(checkPotential(title, description)){
			String subject = Arrays.toString(matchedTerms.toArray()) + " listing found!";
			System.out.println("\n\n" + subject);
	
			if (!postsCache.getPostNames().contains(postName)) {
				sendPM.sendPM(accessToken, subject, post, matchedTerms, isDebug);
				postsCache.addPostName(postName);
			} else {
				System.out.println("Already sent email for this post");
			}
		}

		return null;
	}

	public boolean checkPotential(String title, String description) {
		boolean hasPotential = false;

		if (title.contains("[H]") && title.contains("[W]")) {
			String[] splitTitle = title.split("\\[W\\]", 2);
			if (splitTitle.length <= 1) {
				System.out.println("Error when splitting title: " + title);
			} else {
				String titleHave = splitTitle[0].toLowerCase().trim();
				String titleWant = splitTitle[1].toLowerCase().trim();

				if(wantsMoney(titleWant)){
					description = description.toLowerCase().trim();

					// Remove all instances of whatever is in the excluded words list for each search term
					for(ArrayList<String> aExcludeList : searchItems.values()){
						for (String excludeString : aExcludeList) {
							description = description.replace(excludeString.toLowerCase().trim(), "");
							titleHave = titleHave.replace(excludeString.toLowerCase().trim(), "");
						}
					}

					// Look for the search terms in title and description
					for(String term : searchItems.keySet()){
						String searchTerm = term.toLowerCase().trim();
						if ((titleHave.contains(searchTerm) || description.contains(searchTerm)) && !titleWant.contains(searchTerm)) {
							hasPotential = true;
							matchedTerms.add(term);
						}
					}

				}
			}
		}
		return hasPotential;
	}

	private boolean wantsMoney(String titleWant) {
		boolean isWantsMoney = false;
		if (titleWant.contains("paypal") || titleWant.contains("pp") || titleWant.contains("venmo")
				|| titleWant.contains("cash") || titleWant.contains("money") || titleWant.contains("google")
				|| titleWant.contains("money") || titleWant.contains("$")) {
			isWantsMoney = true;
		}
		return isWantsMoney;
	}
}
