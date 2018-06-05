package shin.watchdog.service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import shin.watchdog.data.Post;
import shin.watchdog.data.RedditSearch;
import shin.watchdog.interfaces.SiteData;

@Service
public class NewRedditPostsService {

	final static Logger logger = LoggerFactory.getLogger(NewRedditPostsService.class);

	private static final int TIMEOUT = 3;
	
	// Date of the previous post in seconds in UTC
	private long previousPostDate;

	private static RequestConfig config = RequestConfig.custom()
        .setConnectTimeout(1 * 1000)
        .setConnectionRequestTimeout(1 * 1000)
        .setSocketTimeout(TIMEOUT * 1000)
        .build();
    
    private static HttpClient httpclient = HttpClientBuilder.create()
        .setDefaultRequestConfig(config)
        .setConnectionManager(new PoolingHttpClientConnectionManager())
        .build();

	private Gson gson;

	private boolean isDebug;

	public NewRedditPostsService(){
		this.gson = new GsonBuilder().create();
	}

    public NewRedditPostsService(boolean isDebug){
		this();
        this.isDebug = isDebug;
    }

	public List<SiteData> makeCall(String subreddit, long interval) {
		//logger.info("making call");
		List<SiteData> newPosts = new ArrayList<>();
        RedditSearch redditSearch = null;

		String tokenURL = 
			"https://www.reddit.com/r/" + subreddit + "/new.json" + (isDebug ? "?limit=5" : "?limit=10");

		HttpGet httpget = new HttpGet(tokenURL);
		httpget.setHeader("User-Agent", "WatchdogSA/0.1 by TimidSA");

		// Execute and get the response.
		HttpEntity entity = null;
		try {
			HttpResponse response = NewRedditPostsService.httpclient.execute(httpget);

			entity = response.getEntity();

			if (response.getStatusLine().getStatusCode() >= 300) {
				throw new Exception("Retrieved bad response code: " + response.getStatusLine());
			} else {
				if (entity != null) {
                    redditSearch = gson.fromJson(EntityUtils.toString(entity), RedditSearch.class);
				} else {
					throw new Exception("Entity from Reddit search GET request was empty");
				}
			}
		} catch (SocketTimeoutException e){
            logger.error("SocketTimeoutException getting new Mechmarket posts - {}", e.getMessage());
        } catch (JsonSyntaxException e){
			logger.error("Error trying to parse Reddit search json", e);
		} catch (IOException e) {
			logger.error("IO Error getting new Reddit posts", e);
        } catch (Throwable e) {
			logger.error("Error getting new Reddit posts", e);
        } finally{
			if(entity != null){
				try {
					EntityUtils.consume(entity);
				} catch (IOException e) {
					logger.error("Error trying to consume Reddit entity", e);
				}
			}
		}

		if(redditSearch != null && !redditSearch.data.children.isEmpty()){
			newPosts.addAll(getNewPosts(redditSearch));
		}

        return newPosts;
	}

	private List<SiteData> getNewPosts(RedditSearch searchResult) {
		List<SiteData> newPosts = new ArrayList<>();

		if(previousPostDate != 0){
			for(Post post : searchResult.data.children){
				// When posts are deleted, older posts creep back up
				// We compare the post date here with the latest post we have in our previous list of posts
				// This to make sure we aren't doing anything on a post we've seen before
				if(post.data.createdUtc > previousPostDate || isDebug){
					newPosts.add(post);
				}
			}   
		}

		this.previousPostDate = searchResult.data.children.get(0).data.createdUtc;

		return newPosts;
	}

}