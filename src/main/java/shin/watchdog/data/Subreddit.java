package shin.watchdog.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import shin.watchdog.actions.SendPrivateMessage;
import shin.watchdog.interfaces.PotentialChecker;
import shin.watchdog.interfaces.SiteData;
import shin.watchdog.utils.WatchdogUtils;

/**
 * Used to hold information about the recent MechMarket posts. Holds a cache of previous posts to compare with newly retrieved posts.
 */
public class Subreddit extends Site{

    private Gson gson = new GsonBuilder().create();

    private boolean isDebug;

    public Subreddit(String id, String boardName, List<SearchItem> searchItems, PotentialChecker potentialChecker, long interval){
        super(id, boardName, searchItems, potentialChecker, interval);
        sdfLocal.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    }

    public Subreddit(String id, String boardName, List<SearchItem> searchItems, PotentialChecker potentialChecker, boolean isDebug){
        super(id, boardName, searchItems, potentialChecker, 0);
        sdfLocal.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        this.isDebug = isDebug;
    }

    @Override
    public long getInterval(){
        return 5;
    }

	@Override
	public LinkedHashMap<String, SiteData> makeCall() {
        RedditSearch redditSearch = null;

        long retrieveDate = System.currentTimeMillis();

		String tokenURL = "https://www.reddit.com/r/" + this.name + "/new.json";

		if(isDebug){
			tokenURL += "?limit=4";
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

                    retrieveDate = sdfGmt.parse(response.getFirstHeader("Date").getValue()).getTime();

                    EntityUtils.consume(entity);
				} else {
					throw new Exception("Entity from Reddit search GET request was null");
				}
			}
		} catch (JsonSyntaxException e){
			System.out.println(sdf.format(resultdate) + " - Error trying to parse Reddit search json: " + e.getMessage());
		} catch (Throwable e) {
			System.out.println(sdf.format(resultdate) + " Error getting new posts - " + e.getMessage() + tokenURL);
		}

		return populatePostsData(redditSearch, retrieveDate);
    }
    
    
	private LinkedHashMap<String, SiteData> populatePostsData(RedditSearch searchResult, long retrieveDate) {
        LinkedHashMap<String, SiteData> newPosts = new LinkedHashMap<>();

       // List<String> newCache = new ArrayList<>();

        if(searchResult != null){
            for(Post post : searchResult.data.children){
                //newCache.add(post.data.id);

                /* This is to handle posts that get deleted right after they're created, 
                or if older posts somehow creep back into the fetched recent posts. 
                If it's an old post, ignore it. Also applies for the first run if nothing was posted at the same time the app starts.*/
                if(Math.abs(retrieveDate - post.data.createdUtc*1000) <= this.interval*1000 || isDebug){
                    // String localPostDate = sdfLocal.format(new Date(post.data.createdUtc));
                    newPosts.put(post.data.id, post);
                } 
                // else {
                //     String localPostDate = sdfLocal.format(new Date(post.data.createdUtc));
                //     System.out.println("Old post found: " + post.data.title + " - " + localPostDate);
                // }
            }   
        }

       // this.cache = newCache;

		return newPosts;
	}

	@Override
	public void sendMessage(List<SiteData> potentialPosts) {
        Set<String> matchesForAllPosts = new HashSet<>();
        StringBuilder message = new StringBuilder();
        for(SiteData siteData : potentialPosts){
            PotentialPost potentialPost = (PotentialPost) siteData;
            Post post = (Post) potentialPost.getSiteData();

            matchesForAllPosts.addAll(potentialPost.getMatchedTerms());

            String hyperlinks = WatchdogUtils.sendPmLink(potentialPost.getMatchedTerms(), post.data.author);

            message.append(String.format(
                "\"%s\"\n/u/%s\n\n\\---- Start " + Arrays.toString(potentialPost.getMatchedTerms().toArray()) + " Post ----/\n\n%s\n\n\\---- End Post ----/\n\n%s", 
                    post.data.title, post.data.author, post.data.selftext, hyperlinks
            ));
    
            message.append("[Leave a comment](" + post.data.url + ")");
            message.append("\n\n*****\n*****\n");
        }

        if(!isDebug){
            SendPrivateMessage.sendPM(Arrays.toString(matchesForAllPosts.toArray()) + " Listings Found", message.toString());
        } else {
            System.out.println(Arrays.toString(matchesForAllPosts.toArray()) + " Listings Found\n" + message.toString());
        }
        
    }
}