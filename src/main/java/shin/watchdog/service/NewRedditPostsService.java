package shin.watchdog.service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import shin.watchdog.data.Post;
import shin.watchdog.data.RedditSearch;
import shin.watchdog.interfaces.SiteData;

public class NewRedditPostsService implements RedditService{

	final static Logger logger = LoggerFactory.getLogger(NewRedditPostsService.class);

    private static final int TIMEOUT = 4;

	private static RequestConfig config = RequestConfig.custom()
        .setConnectTimeout(1 * 1000)
        .setConnectionRequestTimeout(1 * 1000)
        .setSocketTimeout(TIMEOUT * 1000)
        .build();
    
    private static HttpClient httpclient = HttpClientBuilder.create()
        .setDefaultRequestConfig(config)
        .setConnectionManager(new PoolingHttpClientConnectionManager())
        .build();

	// Formatting onjects
    protected SimpleDateFormat sdfLocal;
    protected SimpleDateFormat sdfGmt;
	private Gson gson;
	
	// Time values
	private long catchUpTime;
	private long interval;

	private boolean isDebug;

    public NewRedditPostsService(long interval){
		this.sdfLocal = new SimpleDateFormat("EEE, dd MMM yyyy h:mm:ss a z");
		this.sdfGmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		this.gson = new GsonBuilder().create();
		this.interval = interval;
    }

    public NewRedditPostsService(long interval, boolean isDebug){
		this(interval);
        this.isDebug = isDebug;
    }

	public List<SiteData> makeCall(String subreddit) {
		List<SiteData> newPosts = new ArrayList<>();
        RedditSearch redditSearch = null;

		String tokenURL = 
			"https://www.reddit.com/r/" + subreddit + "/new.json" + (isDebug ? "?limit=1" : "?limit=5");

		HttpGet httpget = new HttpGet(tokenURL);
		httpget.setHeader("User-Agent", "WatchdogSA/0.1 by TimidSA");

		// Execute and get the response.
		long startTime = 0;
		HttpEntity entity = null;
		try {
			startTime = System.currentTimeMillis();
			HttpResponse response = NewRedditPostsService.httpclient.execute(httpget);

			entity = response.getEntity();

			if (response.getStatusLine().getStatusCode() >= 300) {
				throw new Exception("Retrieved bad response code: " + response.getStatusLine());
			} else {
				if (entity != null) {
                    redditSearch = gson.fromJson(EntityUtils.toString(entity), RedditSearch.class);
				} else {
					throw new Exception("Entity from Reddit search GET request was null");
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

		if(redditSearch != null){
			newPosts.addAll(getNewPosts(redditSearch, startTime, this.catchUpTime));
			this.catchUpTime = 0;
		} else {
			this.catchUpTime += this.interval*1000;
		}
        
        return newPosts;
	}

	private List<SiteData> getNewPosts(RedditSearch searchResult, long startTime, long catchUp) {
        List<SiteData> newPosts = new ArrayList<>();

        for(Post post : searchResult.data.children){
            /* This is to handle posts that get deleted right after they're created, 
            or if older posts somehow creep back into the fetched recent posts. 
            If it's an old post, ignore it. Note that this also applies for the first run. */
            if(Math.abs(startTime - post.data.createdUtc*1000) <= (this.interval*1000 + catchUp) || isDebug){
                newPosts.add(post);
            }
        }   

		return newPosts;
	}

}