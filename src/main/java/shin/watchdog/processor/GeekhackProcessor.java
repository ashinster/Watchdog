package shin.watchdog.processor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import shin.watchdog.data.atom.Entry;
import shin.watchdog.data.atom.Feed;
import shin.watchdog.service.GeekhackMessageService;
import shin.watchdog.service.GeekhackPostsService;

public abstract class GeekhackProcessor {
    final static Logger logger = LoggerFactory.getLogger(GeekhackProcessor.class);

    @Value("${isDebug}")
    protected boolean isDebug;

    @Autowired
    protected GeekhackPostsService postsService;

    @Autowired
    protected GeekhackMessageService geekhackMessageService;

    private long previousPubDate;
    private final String rssUrl;
    private final String boardName;

    protected GeekhackProcessor(String rssUrl, String boardName) {
        this.previousPubDate = Instant.now().toEpochMilli();
        this.rssUrl = rssUrl;
        this.boardName = boardName;
    }

    abstract public void processHelper(List<Entry> newPosts);

    abstract public boolean isAlertListEmpty();

    public void process(){
        // Get the feed via rss/atom
        Feed feed = postsService.makeCall(this.rssUrl, this.boardName);

        if (feed != null && feed.getEntry() != null && !feed.getEntry().isEmpty()) {
            // Only get new posts after our last known entry publish date
            List<Entry> newPosts = getNewPosts(feed.getEntry());

            if (!newPosts.isEmpty()) {
                // process through the posts and send an alert if applicable
                processHelper(newPosts);

                this.previousPubDate = Instant.parse(newPosts.get(0).getPublished()).toEpochMilli();
            }
        }
    }

    /**
     * Filters out posts from the current feed that are newer than the previous
     * feed's most recent post
     * 
     * @param fullList The list of post entries from the feed
     * @returns List of posts that are newer than the previous feed's most recent
     *          post
     */
    private List<Entry> getNewPosts(List<Entry> fullList) {
        List<Entry> newPosts = new ArrayList<>();

        for (Entry entry : fullList) {
            if (Instant.parse(entry.getPublished()).toEpochMilli() > this.previousPubDate || this.isDebug) {
                if(!entry.getTitle().startsWith("Re:")){
                    logger.info("New topic found: \"{}\" by {} ({})",
                        entry.getTitle(), entry.getAuthor().getName(), entry.getId());
                }
                newPosts.add(entry);
            }
        }

        return newPosts;
    }
}