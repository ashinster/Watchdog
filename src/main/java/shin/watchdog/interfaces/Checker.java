package shin.watchdog.interfaces;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import shin.watchdog.config.GeekhackConfig;
import shin.watchdog.data.AlertList;
import shin.watchdog.data.Entry;
import shin.watchdog.data.Feed;

@Component
public abstract class Checker{

    final static Logger logger = LoggerFactory.getLogger(Checker.class);

    @Value("${isDebug}")
    protected boolean isDebug;

    public abstract AlertList check(Feed feed, GeekhackConfig config, long previousPubDate, String boardName);

    /**
     * Filters out posts from the current feed that are newer than the previous feed's most recent post
     * @param fullList The list of post entries from the feed
     * @returns List of posts that are newer than the previous feed's most recent post
     */
    public List<Entry> getNewPosts(List<Entry> fullList, long previousPubDate, String boardName){
        List<Entry> newPosts = new ArrayList<>();
        
        for(Entry item : fullList) {
            if(Instant.parse(item.getPublished()).toEpochMilli() > previousPubDate || this.isDebug) {
                if(!item.getTitle().trim().startsWith("Re:") || this.isDebug){
                    logger.info("New {} found: \"{}\" ({})" , boardName, item.getTitle(), item.getId());
                    newPosts.add(item);
                }
            }
        }

        return newPosts;
    }
}