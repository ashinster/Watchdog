package shin.watchdog.scheduled;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TimeZone;

import javax.management.timer.Timer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import shin.watchdog.actions.SendPrivateMessage;
import shin.watchdog.data.Board;
import shin.watchdog.data.Item;
import shin.watchdog.data.Rss;

public class GHInterestChecksRunnable implements Runnable{

    private boolean isDebug;

    private static final String IC = "132";
    private static final String GB = "70";

    /**
     * List which holds Boards we're interested in
     */
    ArrayList<Board> boards;

    /**
     * Used to parse pubdate
     */
    private SimpleDateFormat sdfGmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

    /**
     * Used to get the local time from GMT
     */
    private SimpleDateFormat sdfLocal = new SimpleDateFormat("EEE, dd MMM yyyy h:mm:ss a z");

    private Long currentTime;

    public GHInterestChecksRunnable(){
        boards = new ArrayList<>();
        boards.add(new Board(IC, "Interest Checks"));
        boards.add(new Board(GB, "Group Buys and Preorders"));

        sdfLocal.setTimeZone(TimeZone.getTimeZone("America/New_York")); // Set the local time, derived from pubDate of Item
    }

    public GHInterestChecksRunnable(boolean debug){
        this();
        isDebug = debug;
    }

	@Override
	public void run() {
        try {
            currentTime = System.currentTimeMillis();
			checkForNewPosts();
		} catch (Throwable t) {
			System.out.println("Caught exception in FetchPostTask. StackTrace:");
			t.printStackTrace();
		}
    }
    
    public void checkForNewPosts() {
        StringBuilder body = new StringBuilder();

        // Iterates through all of the interested boards and builds an PM body message
        for(Board board : boards){
            Rss rss = getRss(board.boardId); // Gets the RSS feed only

            if(rss != null){
                // ordered mapping of key-value pairs of topic ID and Item
                LinkedHashMap<String, Item> newItems = getNewPosts(rss, board);

                // Create a list of the topic IDs only, to be used to update the Board cache
                Set<String> newPostIDs = new HashSet<>(newItems.keySet());

                // Remove anything in the new set that's in the cached set, if applicable
                newItems.keySet().removeAll(board.topicsCache);

                // If newItems list is not empty, then new posts were found.
                // If cache for the board is empty, then must be first run. Don't create a PM.
                if(!newItems.keySet().isEmpty() && (isDebug || !board.topicsCache.isEmpty())){
                    body.append(makeMessageForNewPosts(newItems, board.boardName)); // Append to message body
                }
                
                // Update the list we had with the list we just retrieved
                board.topicsCache = newPostIDs;

            } else {
                System.out.println("Rss object was null. See stacktrace");
            }
        }

        // If body isn't empty then new posts were found. Send the PM.
        if(body.length() > 0 && !body.toString().equals("") && !isDebug){
            System.out.println("New Geekhack post found");
            SendPrivateMessage.sendPM("New Posts Found on Geekhack", body.toString());
        } else if(isDebug){
            System.out.println(body.toString());
        }
    }

	private LinkedHashMap<String,Item> getNewPosts(Rss rss, Board board){
        LinkedHashMap<String,Item> newItems = new LinkedHashMap<>(); // A Key-Value set of the topic ID its @Item

        for(Item item : rss.channel.item){
            //https://geekhack.org/index.php?topic=95392.0
            newItems.put(item.guid.substring(37), item); // Only get the topic ID from the guid
        }

        return newItems;
    }

    private String makeMessageForNewPosts(LinkedHashMap<String, Item> newItems, String boardName){
        ArrayList<String> formattedItems = new ArrayList<>();

        StringBuilder entries = new StringBuilder();

        for(Item item : newItems.values()){
            StringBuilder entry = new StringBuilder();
            try {
                String localDate = sdfLocal.format(sdfGmt.parse(item.pubDate));

                /* This is to handle posts that get deleted right after they're created, 
                or if older posts somehow creep back into the fetched recent posts. 
                If it's an old post, ignore it. Also applies for the first run if nothing was posted at the same time the app starts.*/
                if(Math.abs(this.currentTime - sdfGmt.parse(item.pubDate).getTime()) <= Timer.ONE_MINUTE){

                    // Construct the entry with the item title, post date in local time, url, etc.
                    entry.append(item.title).append("\n\n");
                    entry.append(localDate).append("\n\n");
                    entry.append(item.guid).append("\n\n");
                    entry.append("*****\n\n");

                    // Add this entry
                    formattedItems.add(entry.toString());
                } else {
                    System.out.println("Old post found: " + item.title + " - " + localDate);
                }

            } catch (ParseException e) {
                System.out.println("Could not parse Geekhack pubDate.");
                e.printStackTrace();
            }
        }                

        if(!formattedItems.isEmpty()){
            // Append the beginning title and footer for the entries
            formattedItems.add(0, "**New " + boardName + "**\n\n");
            formattedItems.add("*****\n&nbsp;\n\n");

            for(String s : formattedItems){
                entries.append(s);
            }
        }

        return entries.toString();
    }

    private Rss getRss(String board){
        Rss rssFeed = null;

        String debugLimit = isDebug ? "2" : "25";

        BufferedReader in = null;
        try {
            // GET the RSS feed from the URL, which returns XML
            URL oracle = new URL("https://geekhack.org/index.php?action=.xml;sa=news;type=rss2;limit=" + debugLimit + ";board=" + board);
            in = new BufferedReader(new InputStreamReader(oracle.openStream()));

            // Create JAXB objects for unmarshalling
            JAXBContext jaxbContext = JAXBContext.newInstance(Rss.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            // Create the Rss object from the stream
            rssFeed = (Rss) jaxbUnmarshaller.unmarshal(in);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
        } catch (JAXBException e) {
			e.printStackTrace();
        } finally{
            if(in != null){
                try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
        
        return rssFeed;
    }
}