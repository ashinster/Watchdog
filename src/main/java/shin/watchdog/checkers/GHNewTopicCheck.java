package shin.watchdog.checkers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shin.watchdog.config.GeekhackConfig;
import shin.watchdog.data.AlertList;
import shin.watchdog.data.Entry;
import shin.watchdog.data.Feed;
import shin.watchdog.data.GeekhackUser;
import shin.watchdog.interfaces.Checker;

public class GHNewTopicCheck extends Checker{
    final static Logger logger = LoggerFactory.getLogger(GHNewTopicCheck.class);

	@Override
	public AlertList check(Feed rss, GeekhackConfig config, long previousPubDate, String boardName) {
        AlertList alertList = new AlertList();

        if(rss != null && rss.getEntry() != null && !rss.getEntry().isEmpty()){
            List<Entry> newPosts = getNewPosts(rss.getEntry(), previousPubDate, boardName);

            if(!newPosts.isEmpty()){
                // alert people in channel who have role

                List<String> usersToPing = new ArrayList<>();

                for(Entry entry : newPosts){
                    String title = entry.getTitle().trim().toLowerCase();
                    String summary = entry.getSummary().getValue().trim().toLowerCase();
                    String author = entry.getAuthor().getName().trim().toLowerCase();

                    for(GeekhackUser user : config.getUsers()){
                        List<String> userTopics = user.getTopics();

                        for(String topic : userTopics){
                            String term = topic.split(";")[0].trim().toUpperCase();
                            String organizer = topic.split(";")[1].trim().toLowerCase();

                            if(title.contains(term) || summary.contains(term) || author.equalsIgnoreCase(organizer) || isDebug){
                                // Alert these discord IDs
                                if(!isDebug){
                                    if(!usersToPing.contains(user.getId())){
                                        usersToPing.add(user.getId());
                                    }
                                } else {
                                    // Testing alerts, fake mentions
                                    if(!usersToPing.contains("@" + user.getUsername())){
                                        usersToPing.add("@" + user.getUsername());
                                    }
                                }                                
                            }
                        }
                    }
                }

                alertList.setNewPosts(newPosts);
                alertList.setUsersToPing(usersToPing);
            }
        }
        return alertList;
	}
}