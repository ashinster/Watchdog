package shin.watchdog.data;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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
import org.slf4j.MDC;

import shin.watchdog.interfaces.SiteData;
import shin.watchdog.service.GeekhackMessageService;

/**
 * Used to hold information about the board we're interested in. Holds a cache of previous posts to compare with newly retrieved posts.
 */
public class Board extends Site{

    final static Logger logger = LoggerFactory.getLogger(Board.class);

    private static final int TIMEOUT = 10;

	private static RequestConfig config = RequestConfig.custom()
        .setConnectTimeout(1 * 1000)
        .setConnectionRequestTimeout(1 * 1000)
        .setSocketTimeout(TIMEOUT * 1000)
        .build();
    
    private static HttpClient httpclient = HttpClientBuilder.create()
        .setDefaultRequestConfig(config)
        .setConnectionManager(new PoolingHttpClientConnectionManager())
        .build();

    private boolean isDebug;

    private List<String> users;

    private GeekhackMessageService geekhackMessageService;

    private long catchUpTime;

    /**
     * Constructor which takes in the board info and creates an empty topics/posts cache
     * @param id The numeric ID of the board as a String
     * @param boardName The full name of the board
     */
    public Board(String id, String boardName, List<String> users, long interval){
        super(id, boardName, interval);
        this.users = users;
        this.geekhackMessageService = new GeekhackMessageService(boardName);
        sdfLocal.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    }

    public Board(String id, String boardName, List<String> users, boolean isDebug){
        this(id, boardName, users, 0);
        this.geekhackMessageService = new GeekhackMessageService(boardName, true);
        this.isDebug = isDebug;
    }

	@Override
	public void process() {
        MDC.put("uuid", UUID.randomUUID().toString());

        // Get all new posts
        List<SiteData> newItems = makeCall();

        if(!newItems.isEmpty()){
            geekhackMessageService.sendMessage(newItems, users);
        }

        MDC.clear();
        return;
	}

	public List<SiteData> makeCall() {
        List<SiteData> newPosts = new ArrayList<>();
        Rss rssFeed = null;

        String limit = isDebug ? "2" : "5";
        String tokenURL 
            = "https://geekhack.org/index.php?action=.xml;sa=news;type=rss2;limit=" + limit + ";board=" + this.id;

		HttpGet httpget = new HttpGet(tokenURL);

        // Execute and get the response.
        long startTime = 0;
        HttpEntity entity = null;
		try {
            startTime = System.currentTimeMillis();
            HttpResponse response = Board.httpclient.execute(httpget);

            entity = response.getEntity();

			if (response.getStatusLine().getStatusCode() >= 300) {
				throw new Exception("Retrieved bad response code: " + response.getStatusLine());
			} else {
				if (entity != null) {
                    // Create JAXB objects for unmarshalling
                    JAXBContext jaxbContext = JAXBContext.newInstance(Rss.class);
                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        
                    // Create the Rss object from the stream
                    rssFeed = (Rss) jaxbUnmarshaller.unmarshal(response.getEntity().getContent());

                    //retrieveDate = sdfGmt.parse(response.getFirstHeader("Date").getValue()).getTime();
				} else {
					throw new Exception("Entity from Geekhack RSS GET request was null for " + this.name);
				}
			}
        } catch (SocketTimeoutException e){
            //logger.error("SocketTimeoutException getting new {} - {}", this.name, e.getMessage());
        } catch (IOException e) {
            //logger.error("IO Exception getting new " + this.name, e);
        } catch (JAXBException e) {
            logger.error("Error Unmarshalling rss feed for " + this.name, e);
        } catch (Throwable e) {
            logger.error("Error getting new " + this.name, e);
        } finally{
			if(entity != null){
				try {
					EntityUtils.consume(entity);
				} catch (IOException e) {
                    logger.error("Error trying to consume Geekhack entity for " + this.name, e);
				}
            }
        }

        if(rssFeed != null){
            newPosts.addAll(getNewPosts(rssFeed.channel.item, startTime, this.catchUpTime));
            this.catchUpTime = 0;
        } else {
            this.catchUpTime += this.interval*1000;
            //logger.warn("Next request will get posts from at least {} seconds ago", this.interval + Math.abs(this.prevRetrieveTime/1000));
        }

        return newPosts;
    }

    public List<SiteData> getNewPosts(List<Item> fullList, long startTime, long catchUpTime){
        List<SiteData> newPosts = new ArrayList<>();

        try{
            for(Item post : fullList){
                if(Math.abs(startTime - sdfGmt.parse(post.pubDate).getTime()) <= (this.interval*1000 + catchUpTime)){
                    newPosts.add(post);
                    logger.info("New {} found: {}", this.name, post.title);
                }
            }
        } catch (ParseException e){
            logger.error("Error parsing a post's publish date for " + this.name, e);
        }

        return newPosts;
    }
}