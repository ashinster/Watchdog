package shin.watchdog.processor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import shin.watchdog.checkers.MechMarketChecker;
import shin.watchdog.config.MechmarketConfig;
import shin.watchdog.data.MechmarketPost;
import shin.watchdog.data.Post;
import shin.watchdog.data.RedditSearch;
import shin.watchdog.data.WatchdogUser;
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

    private String subredditName;
    
    private long previousPostDate;

    public MechmarketProcessor(String subredditName){
        this.subredditName = subredditName;
        this.previousPostDate = System.currentTimeMillis()/1000;
    }

    public void process(){
        RedditSearch redditSearch = newPostsService.makeCall(this.subredditName);

        if(redditSearch != null) {
            List<Post> newPosts = getNewPosts(redditSearch.data.children);

            // If there are any new posts, then check potential of it
            if(!newPosts.isEmpty()){
                // Search each user's search items
                for (WatchdogUser user : this.config.getUsers()){
                    // Get the only the potential posts for each the user
                    processForUser(newPosts, user);
                }
            }
        }
    }

    private List<Post> getNewPosts(List<Post> searchResult) {
        List<Post> newPosts = new ArrayList<>();
        
        long newestPost = previousPostDate;
        for(Post post : searchResult){
            // When posts are deleted, older posts creep back up
            // We compare the post date here with the latest post we have in our previous list of posts
            // This to make sure we aren't doing anything on a post we've seen before
            if(post.data.createdUtc > previousPostDate){
                newPosts.add(post);

                if(post.data.createdUtc > newestPost){
                    newestPost = post.data.createdUtc;
                }
            }
        }

        this.previousPostDate = newestPost;

		return newPosts;
	}

	private void processForUser(List<Post> newPosts, WatchdogUser user) {

        MechMarketChecker checker = new MechMarketChecker();
        List<MechmarketPost> potentialPosts = checker.getPotentialPosts(newPosts, user);
        
        // send a message to the user about the potential posts
        if(!potentialPosts.isEmpty()){
            redditMessageService.sendMessage(this.subredditName, potentialPosts, user.getSendto());
        }
    }
}