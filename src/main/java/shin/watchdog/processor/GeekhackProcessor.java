package shin.watchdog.processor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import shin.watchdog.data.GeekhackThread;
import shin.watchdog.data.atom.Entry;
import shin.watchdog.data.atom.Feed;
import shin.watchdog.service.GeekhackMessageService;
import shin.watchdog.service.GeekhackPostsService;

import java.util.stream.Collectors;

/**
 * Class which processes and alerts for Geekhack posts and/or threads
 */
public abstract class GeekhackProcessor {
    final static Logger logger = LoggerFactory.getLogger(GeekhackProcessor.class);

    @Value("${isDebug:false}")
    protected boolean isDebug;

    @Autowired
    protected GeekhackPostsService postsService;

    @Autowired
    protected GeekhackMessageService geekhackMessageService;

    /**
     * The RSS/ATOM URL to retrieve the Geekhack data
     */
    protected final String rssUrl;

    /**
     * The max number of threads to retrieve
     */
    protected final String limit;

    /**
     * The Geekhack board ID to point to
     */
    protected final String boardId;

    /**
     * The order of posts to retrieve
     */
    protected final String subAction;

    /**
     * Name of the Geekhack board
     */
    protected final String boardName;
    
    /**
     * Discord role ID to ping
     */
    protected final String alertRoleId;

    /**
     * Discord webhook url to post message in a channel
     */
    protected final String alertRoleChannelUrl;

    /**
     * Creates a Geekhack Processor which will retrieve n posts from a specific board, and alert if a new thread was found
     * @param rssUrl The Geekhack RSS/ATOM endpoint
     * @param boardId The Geekhack board ID to get the posts from
     * @param limit The max number of posts/threads to retrieve from the endpoint
     * @param subAction The order of the posts/threads
     */
    protected GeekhackProcessor(String boardName, String rssUrl, String boardId, String limit, String subAction, String alertRoleId, String alertRoleChannelUrl) {
        this.boardName = boardName;
        this.boardId = boardId;
        this.limit = limit;
        this.subAction = subAction;
        this.rssUrl = rssUrl + String.format(";boards=%s;limit=%s;sa=%s", boardId, limit, subAction);
        this.alertRoleId = alertRoleId;
        this.alertRoleChannelUrl = alertRoleChannelUrl;
    }

    /**
     * Retrieves the new posts sends alerts
     */
    public void process() {
        // Get the feed via rss/atom
        Feed feed = postsService.makeCall(this.rssUrl, this.boardName);

        // Check if feed is empty for whatever reason
        if (feed != null && feed.getEntry() != null && !feed.getEntry().isEmpty()) {
            List<GeekhackThread> newThreads = getNewPosts(feed.getEntry());

            // Do a specific action based on the new posts found
            if(!newThreads.isEmpty()){
                processHelper(newThreads);
            }
        }
    }

    /**
     * Filter out old posts we've already seen out of the retrieved feed
     * @param fullList The list of entries from the atom feed
     * @return List of new GeekhackThreads that we should alert for
     */
    public List<GeekhackThread> getNewPosts(List<Entry> fullList){
        List<GeekhackThread> newThreads = new ArrayList<>();

        // Filter out the old threads
        newThreads = fullList.stream()
            .filter(entry -> filter(entry))
            .map(entry -> new GeekhackThread(entry))
            .peek(geekhackThread -> logger.info("New thread found: \"{}\" by {} ({})", geekhackThread.getTitle(), geekhackThread.getAuthor(), geekhackThread.getId()))
            .collect(Collectors.toList());

        return newThreads;        
    }

    /**
     * Used to determine if a post is old or already been seen
     * @param entry The atom entry
     * @return True if the post is new
     */
    abstract boolean filter(Entry entry);
    
    /**
     * Helper method for the process method which will perform the logic on the RSS/ATOM data
     * @param newPosts The list of new posts/threads
     */
    abstract public void processHelper(List<GeekhackThread> feed);

    /**
     * Sends an alert for the list of new threads to the configured Discord channel
     * and role
     * 
     * @param newThreads The list of new threads
     */
    abstract boolean sendAlert(List<GeekhackThread> newThreads);

}