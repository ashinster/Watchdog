package shin.watchdog.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import shin.watchdog.checkers.MechMarketChecker;
import shin.watchdog.interfaces.PotentialChecker;
import shin.watchdog.interfaces.SiteData;
import shin.watchdog.service.NewRedditPostsService;
import shin.watchdog.service.RedditMessageService;

/**
 * Used to hold information about the recent MechMarket posts. Holds a cache of previous posts to compare with newly retrieved posts.
 */
public class Subreddit extends Site{

    final static Logger logger = LoggerFactory.getLogger(Subreddit.class);

    private boolean isDebug;

    private PotentialChecker checker;

    private Map<String, List<SearchItem>> searchItemsForUsers;

    private NewRedditPostsService newPostsService;
    private RedditMessageService redditMessageService;

    public Subreddit(String id, String boardName, Map<String, List<SearchItem>> searchItemsForUsers, PotentialChecker checker, long interval){
        super(id, boardName, interval);
        this.searchItemsForUsers = searchItemsForUsers;
        this.checker = (MechMarketChecker) checker;
        this.newPostsService = new NewRedditPostsService(this.interval);
        this.redditMessageService = new RedditMessageService();
        sdfLocal.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    }

    public Subreddit(String id, String boardName, Map<String, List<SearchItem>> searchItemsForUsers, PotentialChecker checker, boolean isDebug){
        this(id, boardName, searchItemsForUsers, checker, 0);
        this.newPostsService = new NewRedditPostsService(this.interval, isDebug);
        this.redditMessageService = new RedditMessageService(isDebug);
        this.isDebug = isDebug;
    }

    @Override
	public void process() {
        MDC.put("uuid", UUID.randomUUID().toString());

        List<SiteData> newPosts = newPostsService.makeCall(this.name);

        // If there are any new posts, then check potential of it
        if(!newPosts.isEmpty()){
            // Search each user's search items
            for (Entry<String, List<SearchItem>> entry : searchItemsForUsers.entrySet()){
                // Get the potential posts only for the user
                List<SiteData> potentialPosts = checker.getPotentialPosts(newPosts, entry.getValue());

                // send a message to the user about the potential posts
                if(!potentialPosts.isEmpty()){
                    sendMessage(potentialPosts, Arrays.asList(entry.getKey()));
                }
            }
        }

        MDC.clear();
        return;
	}    

	public void sendMessage(List<SiteData> potentialPosts, List<String> users) {
        redditMessageService.sendMessage(potentialPosts, users);
    }
}