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

public class GeekhackProcessor {
    final static Logger logger = LoggerFactory.getLogger(GeekhackProcessor.class);

    @Value("${isDebug}")
    protected boolean isDebug;

    @Autowired
    private GeekhackPostsService postsService;

    @Autowired
    private GeekhackMessageService geekhackMessageService;

    private String boardName;

    private long previousPubDate;

    private String roleId;
    private String channelUrl;

    private String rssUrl;

    private Checker checker;

    public GeekhackProcessor(String rssUrl, String boardName, String channelUrl, String roleId, Checker checker) {
        this.previousPubDate = Instant.now().toEpochMilli();
        this.rssUrl = rssUrl;
        this.boardName = boardName;
        this.channelUrl = channelUrl;
        this.roleId = roleId;
        this.checker = checker;
    }

    public void process() {
        // Get the feed via rss/atom
        Feed rss = postsService.makeCall(this.rssUrl, this.boardName);

        if (rss != null && rss.getEntry() != null && !rss.getEntry().isEmpty()) {
            List<Entry> newPosts = getNewPosts(rss.getEntry());

            if (!newPosts.isEmpty()) {
                List<Entry> postsToRemove = new ArrayList<>();

                // list of users/roles to alert
                Set<String> usersToPing = new HashSet<>();

                // Check each entry to see if we need to alert a role
                for (Entry entry : newPosts) {
                    if (checker.check(entry)) {
                        // If there's a match, then get the role to alert for
                        String author = entry.getAuthor().getName().trim().toLowerCase();
                        String roleId = checker.roleIdForTopic(author);

                        // Add the role to ping
                        usersToPing.add(roleId);
                    } else {
                        postsToRemove.add(entry);
                    }
                }

                // remove posts from list for updated threads process
                if(roleId.isEmpty()){
                    newPosts.removeAll(postsToRemove);
                }

                if(!newPosts.isEmpty()){
                    geekhackMessageService.sendMessage(boardName, newPosts, usersToPing, channelUrl, roleId);
                }

                // Set the previous publish date to the most recent post in the list
                previousPubDate = Instant.parse(newPosts.get(0).getPublished()).toEpochMilli();
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
    public List<Entry> getNewPosts(List<Entry> fullList) {
        List<Entry> newPosts = new ArrayList<>();

        for (Entry item : fullList) {
            if (Instant.parse(item.getPublished()).toEpochMilli() > this.previousPubDate || this.isDebug) {
                logger.info("New {} found: \"{}\" ({})", boardName, item.getTitle(), item.getId());
                newPosts.add(item);
            }
        }

        return newPosts;
    }
}