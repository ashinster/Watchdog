package shin.watchdog.processor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import shin.watchdog.checkers.MechMarketChecker;
import shin.watchdog.config.MechmarketConfig;
import shin.watchdog.data.WatchdogUser;
import shin.watchdog.interfaces.SiteData;
import shin.watchdog.service.NewRedditPostsService;
import shin.watchdog.service.RedditMessageService;

public class MechmarketProcessor {
    final static Logger logger = LoggerFactory.getLogger(MechmarketProcessor.class);

    @Autowired
    private MechmarketConfig config;

    @Autowired
    private NewRedditPostsService newPostsService;

    @Autowired
    private RedditMessageService redditMessageService;

    public void process(){
        MDC.put("uuid", UUID.randomUUID().toString());
        List<SiteData> newPosts = newPostsService.makeCall("mechmarket", config.getInterval());

        // If there are any new posts, then check potential of it
        if(!newPosts.isEmpty()){
            // Search each user's search items
            for (WatchdogUser user : this.config.getUsers()){
                // Get the potential posts only for the user
                processForUser(newPosts, user);
            }
        }
        MDC.clear();
    }

	private void processForUser(List<SiteData> newPosts, WatchdogUser user) {

        MechMarketChecker checker = new MechMarketChecker();
        List<SiteData> potentialPosts = checker.getPotentialPosts(newPosts, user);
        
        // send a message to the user about the potential posts
        if(!potentialPosts.isEmpty()){
            redditMessageService.sendMessage(potentialPosts, Arrays.asList(user.getSendto()));
        }
    }
}