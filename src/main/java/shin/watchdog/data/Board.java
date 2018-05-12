package shin.watchdog.data;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import shin.watchdog.actions.SendPrivateMessage;
import shin.watchdog.interfaces.SiteData;

/**
 * Used to hold information about the board we're interested in. Holds a cache of previous posts to compare with newly retrieved posts.
 */
public class Board extends Site{

    private boolean isDebug;

    /**
     * Constructor which takes in the board info and creates an empty topics/posts cache
     * @param id The numeric ID of the board as a String
     * @param boardName The full name of the board
     */
    public Board(String id, String boardName, long interval){
        super(id, boardName, null, null, interval);
        sdfLocal.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    }

    public Board(String id, String boardName, boolean isDebug){
        super(id, boardName, null, null, 0);
        sdfLocal.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        this.isDebug = true;
    }

    @Override
    public long getInterval(){
        return 60;
    }

	@Override
	public LinkedHashMap<String, SiteData> makeCall() {
        Rss rssFeed = null;		

        long retrieveDate = System.currentTimeMillis();

        String limit = isDebug ? "2" : "25";
        
        String tokenURL = "https://geekhack.org/index.php?action=.xml;sa=news;type=rss2;limit=" + limit + ";board=" + this.id;

		HttpGet httpget = new HttpGet(tokenURL);

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
                    // Create JAXB objects for unmarshalling
                    JAXBContext jaxbContext = JAXBContext.newInstance(Rss.class);
                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        
                    // Create the Rss object from the stream
                    rssFeed = (Rss) jaxbUnmarshaller.unmarshal(response.getEntity().getContent());

                    retrieveDate = sdfGmt.parse(response.getFirstHeader("Date").getValue()).getTime();

                    EntityUtils.consume(entity);
				} else {
					throw new Exception("Entity from Geekhack RSS GET request was null");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("\n" + sdf.format(resultdate) + " - IO Exception getting new posts: " + e.getMessage());
        } catch (JAXBException e) {
            e.printStackTrace();
			System.out.println("\n" + sdf.format(resultdate) + " - Error Unmarshalling rss feed: " + e.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
			System.out.println("\n" + sdf.format(resultdate) + " - Error getting new posts: " + e.getMessage());
        }
        
        return getNewPosts(rssFeed, retrieveDate);
    }
    
    private LinkedHashMap<String, SiteData> getNewPosts(Rss rss, long retrieveDate){
        LinkedHashMap<String, SiteData> newItems = new LinkedHashMap<>(); // A Key-Value set of the topic ID its @Item

        List<String> newCache = new ArrayList<>();

        if(rss != null){
            try{
                for(Item item : rss.channel.item){
                    newCache.add(item.guid.substring(37));

                    /* This is to handle posts that get deleted right after they're created, 
                    or if older posts somehow creep back into the fetched recent posts. 
                    If it's an old post, ignore it. Also applies for the first run if nothing was posted at the same time the app starts.*/
                    if(Math.abs(retrieveDate - sdfGmt.parse(item.pubDate).getTime()) <= this.interval*1000 || isDebug){
                        //https://geekhack.org/index.php?topic=95392.0
                        newItems.put(item.guid.substring(37), item); // Only get the topic ID from the guid
                    } 
                    // else if(!cache.isEmpty()){
                    //     String localDate = sdfLocal.format(sdfGmt.parse(item.pubDate));
                    //     System.out.println("Old post found: " + item.title + " - " + localDate);
                    // }
                }   
            } catch (ParseException e){
                System.out.println("Could not parse Geekhack pubDate.");
                e.printStackTrace();
            }
        }

        this.cache = newCache;

        return newItems;
    }

	@Override
	public void sendMessage(List<SiteData> potentialPosts) {
        ArrayList<String> formattedItems = new ArrayList<>();

        StringBuilder entries = new StringBuilder();

        for(SiteData siteData : potentialPosts){
            Item post = (Item) siteData;
            StringBuilder entry = new StringBuilder();
            try {
                String localDate = sdfLocal.format(sdfGmt.parse(post.pubDate));
                entry.append(post.title).append("\n\n");
                entry.append(localDate).append("\n\n");
                entry.append(post.guid).append("\n\n");
                entry.append("*****\n\n");

                // Add this entry
                formattedItems.add(entry.toString());

            } catch (ParseException e) {
                System.out.println("Could not parse Geekhack pubDate.");
                e.printStackTrace();
            }
        }                

        if(!formattedItems.isEmpty()){
            // Append the beginning title and footer for the entries
            formattedItems.add(0, "**New " + this.name + "**\n\n");
            formattedItems.add("*****\n&nbsp;\n\n");

            for(String s : formattedItems){
                entries.append(s);
            }
        }

        if(!isDebug){
            SendPrivateMessage.sendPM("New " + this.name + " Found on Geekhack", entries.toString());
        } else {
            System.out.println("New " + this.name + " Found on Geekhack\n" + entries.toString());
        }
        
	}
}