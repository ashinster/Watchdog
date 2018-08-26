package shin.watchdog.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import shin.watchdog.config.GeekhackConfig;
import shin.watchdog.data.AlertList;
import shin.watchdog.data.Entry;
import shin.watchdog.data.Feed;
import shin.watchdog.data.GeekhackUser;
import shin.watchdog.interfaces.Checker;
import shin.watchdog.service.GeekhackMessageService;
import shin.watchdog.service.GeekhackPostsService;

public class GeekhackProcessor{
    final static Logger logger = LoggerFactory.getLogger(GeekhackProcessor.class);

    @Autowired
    private GeekhackConfig config;

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
    
    public GeekhackProcessor(String rssUrl, String boardName, String channelUrl, String roleId, Checker checker){
        this.rssUrl = rssUrl;
        this.boardName = boardName;
        this.previousPubDate = Instant.now().toEpochMilli();
        this.channelUrl = channelUrl;
        this.roleId = roleId;
        this.checker = checker;
    }

    public void process(){
        // Get the feed via rss/atom
        Feed rss = postsService.makeCall(this.rssUrl, this.boardName);

        // Parse the posts from the feed we just retrieved
        AlertList alertList = checker.check(rss, config, previousPubDate, boardName);

        if(alertList.getNewPostsList() != null){
            // Send message for the new posts
            geekhackMessageService.sendMessage(
                boardName, 
                alertList.getNewPostsList(), 
                alertList.getUsersToPingList(), 
                channelUrl, roleId);

            // Set the previous publish date to the most recent post in the list
            previousPubDate = Instant.parse(alertList.getNewPostsList().get(0).getPublished()).toEpochMilli();
        }
    }
}