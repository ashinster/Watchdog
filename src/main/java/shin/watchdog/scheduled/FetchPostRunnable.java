package shin.watchdog.scheduled;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import shin.watchdog.actions.SendPrivateMessage;
import shin.watchdog.checkers.MechMarketChecker;
import shin.watchdog.data.Post;
import shin.watchdog.data.PotentialPost;
import shin.watchdog.data.RedditSearch;
import shin.watchdog.data.SearchItem;
import shin.watchdog.data.Subreddit;
import shin.watchdog.utils.WatchdogUtils;

public class FetchPostRunnable implements Runnable {

	public static String accessToken;

	HttpClient httpclient = HttpClients.createDefault();

	boolean isDebug;

	ArrayList<SearchItem> searchItems;

	private ArrayList<Subreddit> subreddits;

	long currentTime;

    /**
     * Used to get the local time from GMT
     */
    private SimpleDateFormat sdfLocal = new SimpleDateFormat("EEE, dd MMM yyyy h:mm:ss a z");

	private Gson gson = new GsonBuilder().create();

	public FetchPostRunnable(ArrayList<SearchItem> searchItems) {
		this.searchItems = searchItems;

		Map<String, SearchItem> searchMap = new HashMap<>();
		for(SearchItem sI : searchItems){
			searchMap.put(sI.searchTerm, sI);
		}

		subreddits = new ArrayList<>();
		subreddits.add(new Subreddit("t5_2vgng", "MechMarket", searchMap, new MechMarketChecker()));

		sdfLocal.setTimeZone(TimeZone.getTimeZone("America/New_York")); // Set the local time, derived from pubDate of Item
	}

	public FetchPostRunnable(ArrayList<SearchItem> searchItems, boolean isDebug) {
		this(searchItems);
		this.isDebug = isDebug;
	}

	@Override
	public void run() {
		try {
			currentTime = System.currentTimeMillis();
			fetchPostsHelper();
		} catch (Throwable t) {
			System.out.println("Caught exception in FetchPostTask. StackTrace:");
			t.printStackTrace();
		}
	}

	private void fetchPostsHelper() {
		for(Subreddit sub : subreddits){
			// Make the call to get the newest posts
			RedditSearch searchResult = makeCall();

			if (searchResult != null) {
				// ordered mapping of key-value pairs of post ID and Post
				Map<String, Post> newItems = populatePostsData(searchResult);

				// Create a list of the topic IDs only, to be used to update the Board cache
				Set<String> newPostIDs = new HashSet<>(newItems.keySet());

				// Remove posts that were in the cache, remaining posts are new posts
				newItems.keySet().removeAll(sub.postsCache);

				if (!newItems.isEmpty() && (isDebug || !sub.postsCache.isEmpty())) {
					List<PotentialPost> potentialPosts = getPotentialPosts(newItems, sub);

					// The left over list is the list of potential items, might be empty
					if(!potentialPosts.isEmpty()) {
						Set<String> matchesForAllPosts = new HashSet<>();
						StringBuilder message = new StringBuilder();
						for(PotentialPost post : potentialPosts){
							matchesForAllPosts.addAll(post.getMatchedTerms());

							String hyperlinks = WatchdogUtils.sendPmLink(post.getMatchedTerms(), post.getAuthor());

							message.append(String.format(
								"\"%s\"\n/u/%s\n\n\\---- Start " + Arrays.toString(post.getMatchedTerms().toArray()) + " Post ----/\n\n%s\n\n\\---- End Post ----/\n\n%s\n\n", 
									post.getTitle(), post.getAuthor(), post.getDescription(), hyperlinks
							));
					
							message.append("[Leave a comment](" + post.getUrl() + ")");
							message.append("\n\n*****\n*****\n");
						}

						if(!isDebug){
							SendPrivateMessage.sendPM(Arrays.toString(matchesForAllPosts.toArray()) + " Listings Found", message.toString());
						} else {
							System.out.println(Arrays.toString(matchesForAllPosts.toArray()) + " Listings Found\n" + message.toString());
						}
					}					
				}

				// Update cache with posts we just retrieved, so we always have updated posts
				sub.postsCache = newPostIDs;
			} else {
                System.out.println("Subreddit object was null");
            }
		}
	}

	private List<PotentialPost> getPotentialPosts(Map<String, Post> newItems, Subreddit sub){
		List<PotentialPost> potentialPosts = new ArrayList<>();

		for(Post post : newItems.values()){
			PotentialPost potentialPost = new PotentialPost(post);
			if(Math.abs(this.currentTime - (post.data.createdUtc*1000)) <= 5000){ 
				for(String searchKey : sub.searchItems.keySet()){
					sub.setSearchItem(searchKey);
					if(sub.checkPotential(post.data.title, post.data.selftext) || isDebug){
						potentialPost.addMatchedTerm(searchKey);
					}
				}
			} else { 
				System.out.println("old post found: " + post.data.title + " - " + sdfLocal.format(new Date(post.data.createdUtc*1000)));
			}

			// The post has at least one matched term, so thus it is a potential post
			if(!potentialPost.getMatchedTerms().isEmpty()){
				potentialPosts.add(potentialPost);
			}
		}

		return potentialPosts;
	}

	private LinkedHashMap<String, Post> populatePostsData(RedditSearch searchResult) {
		LinkedHashMap<String, Post> newPosts = new LinkedHashMap<>();

		for(Post post : searchResult.data.children) {
			newPosts.put(post.data.id, post);
		}

		return newPosts;
	}

	private RedditSearch makeCall() {
		RedditSearch redditSearch = null;

		String tokenURL = "https://www.reddit.com/r/mechmarket/new.json?restrict_sr=on&sort=new&t=all";

		if(isDebug){
			tokenURL += "&limit=4";
		}

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
				throw new Exception("Error from response: " + response.getStatusLine());
			} else {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					redditSearch = gson.fromJson(EntityUtils.toString(entity), RedditSearch.class);
				} else {
					throw new Exception("Entity from Reddit search GET request was null");
				}
			}
		} catch (JsonSyntaxException e){
			System.out.println("\n" + sdf.format(resultdate) + " - Error trying to parse Reddit search json: " + e.getMessage());
		} catch (Throwable e) {
			System.out.println("\n" + sdf.format(resultdate) + " - Error getting new posts: " + e.getMessage());
		}

		return redditSearch;
	}
}
