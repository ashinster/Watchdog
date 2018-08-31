package shin.watchdog.processor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import shin.watchdog.data.Entry;
import shin.watchdog.data.Feed;
import shin.watchdog.interfaces.Checker;
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
    
    protected long previousPubDate;
    protected String rssUrl;
    protected String boardName;
    protected String channelUrl;
    protected String roleId;
    
    public GeekhackProcessor(String rssUrl, String boardName, String channelUrl, String roleId) {
        this.previousPubDate = Instant.now().toEpochMilli();
        this.rssUrl = rssUrl;
        this.boardName = boardName;
        this.channelUrl = channelUrl;
        this.roleId = roleId;
    }

    abstract public void process();

    /**
     * Filters out posts from the current feed that are newer than the previous
     * feed's most recent post
     * 
     * @param fullList The list of post entries from the feed
     * @returns List of posts that are newer than the previous feed's most recent
     *          post
     */
    public List<Entry> getNewPosts(List<Entry> fullList) {
        List<Entry> newPosts = new ArrayList<>();

        for (Entry item : fullList) {
            if (Instant.parse(item.getPublished()).toEpochMilli() > this.previousPubDate || this.isDebug) {
                newPosts.add(item);
            }
        }

        return newPosts;
    }
}