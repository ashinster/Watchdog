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
import shin.watchdog.data.Entry;
import shin.watchdog.data.Feed;
import shin.watchdog.service.GeekhackMessageService;
import shin.watchdog.service.GeekhackPostsService;

public class GeekhackProcessor{
    final static Logger logger = LoggerFactory.getLogger(GeekhackProcessor.class);

    @Value("${isDebug}")
    private boolean isDebug;

    @Autowired
    private GeekhackConfig config;

    @Autowired
    private GeekhackPostsService postsService;

    @Autowired
    private GeekhackMessageService geekhackMessageService;

    private String boardName;
    private String boardId;

	private long previousPubDate;
    
    public GeekhackProcessor(String boardId, String boardName){
        this.boardId = boardId;
        this.boardName = boardName;
        this.previousPubDate = Instant.now().toEpochMilli();
    }

    public void process(){
        //logger.info("PREVIOUS PUB DATE {}", new SimpleDateFormat("EEE, dd MMM yyyy h:mm:ss a z").format(new Date(this.previousPubDate)));

        Feed rss = postsService.makeCall(this.boardId, this.boardName);

        if(rss != null && rss.getEntry() != null && !rss.getEntry().isEmpty()){
            List<Entry> newPosts = getNewPosts(rss.getEntry());

            if(!newPosts.isEmpty()){
                // alert each subscribed user on the new posts
                for (String user : this.config.getUsers()){
                    geekhackMessageService.sendMessage(this.boardName, newPosts, user);
                }

                this.previousPubDate = Instant.parse(newPosts.get(0).getPublished()).toEpochMilli();
            }
        }
    }

    /**
     * Filters out posts from the current feed that are newer than the previous feed's most recent post
     * @param fullList The list of post entries from the feed
     * @returns List of posts that are newer than the previous feed's most recent post
     */
    public List<Entry> getNewPosts(List<Entry> fullList){
        List<Entry> newPosts = new ArrayList<>();
        
        for(Entry item : fullList) {
            if(Instant.parse(item.getPublished()).toEpochMilli() > this.previousPubDate || isDebug) {
                if(!item.getTitle().trim().startsWith("Re:")){
                    logger.info("New {} found: \"{}\" ({})" , this.boardName, item.getTitle(), item.getId());
                    newPosts.add(item);
                }
            }
        }

        return newPosts;
    }
}