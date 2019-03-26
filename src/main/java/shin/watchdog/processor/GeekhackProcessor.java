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

    protected final String boardName;

    private List<String> lastRetrievedPosts;

    /**
     * Creates a Geekhack Processor which will retrieve n posts from a specific board, and alert if a new thread was found
     * @param rssUrl The Geekhack RSS/ATOM endpoint
     * @param boardId The Geekhack board ID to get the posts from
     * @param limit The max number of posts/threads to retrieve from the endpoint
     * @param subAction The order of the posts/threads
     */
    protected GeekhackProcessor(String boardName, String rssUrl, String boardId, String limit, String subAction) {
        this.boardName = boardName;
        this.boardId = boardId;
        this.limit = limit;
        this.subAction = subAction;
        this.rssUrl = rssUrl + String.format(";boards=%s;limit=%s;sa=%s", boardId, limit, subAction);
        this.lastRetrievedPosts = new ArrayList<>();
    }

    /**
     * Helper method for the process method which will perform the logic on the RSS/ATOM data
     * @param newPosts The list of new posts/threads
     */
    abstract public void processHelper(List<GeekhackThread> newPosts);

    /**
     * Retrieves the new posts sends alerts
     */
    public void process() {
        // Get the feed via rss/atom
        Feed feed = postsService.makeCall(this.rssUrl, this.boardName);

        if (feed != null && feed.getEntry() != null && !feed.getEntry().isEmpty()) {
            // Only get new posts after our last known entry publish date
            List<GeekhackThread> newThreads = getNewPosts(feed.getEntry());

            if (!newThreads.isEmpty()) {
                processHelper(newThreads);
            }
        }
    }

    /**
     * Filters out posts/threads that are older than the previous most recent item
     * 
     * @param fullList The list of posts/threads from the Geekhack RSS/ATOM feed
     * @return List of new posts/threads
     */
    private List<GeekhackThread> getNewPosts(List<Entry> fullList) {

        List<GeekhackThread> newThreads = new ArrayList<>();

        if(this.lastRetrievedPosts.isEmpty()){
            this.lastRetrievedPosts = fullList.stream()
                .map(entry -> entry.getId().substring(37))
                .collect(Collectors.toList());
        } else {
            List<String> newThreadIds = new ArrayList<>();
            
            newThreads = fullList.stream()
                .peek(entry -> newThreadIds.add(entry.getId().substring(37)))
                .filter(entry -> !this.lastRetrievedPosts.contains(entry.getId().substring(37)) || isDebug)
                .filter(entry -> !entry.getTitle().startsWith("Re:"))
                .peek(entry -> logger.info("New thread found: \"{}\" by {} ({})", entry.getTitle(), entry.getAuthor().getName(), entry.getId()))
                .map(entry -> new GeekhackThread(entry))
                .collect(Collectors.toList());

            this.lastRetrievedPosts = newThreadIds;
        }

        return newThreads;
    }
}