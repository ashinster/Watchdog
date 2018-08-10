package shin.watchdog.service;

import java.io.IOException;
import java.net.SocketTimeoutException;

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

import shin.watchdog.data.RedditSearch;

@Service
public class NewRedditPostsService {

	final static Logger logger = LoggerFactory.getLogger(NewRedditPostsService.class);

	private static final int TIMEOUT = 3;

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

	public RedditSearch makeCall(String subreddit) {
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
            //logger.error("SocketTimeoutException getting new Mechmarket posts - {}", e.getMessage());
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

        return redditSearch;
	}

}