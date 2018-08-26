package shin.watchdog.checkers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shin.watchdog.config.GeekhackConfig;
import shin.watchdog.data.AlertList;
import shin.watchdog.data.Entry;
import shin.watchdog.data.Feed;
import shin.watchdog.data.TopicSubscription;
import shin.watchdog.interfaces.Checker;

public class GHUpdatedTopicCheck extends Checker{
    final static Logger logger = LoggerFactory.getLogger(GHUpdatedTopicCheck.class);

	@Override
	public AlertList check(Feed rss, GeekhackConfig config, long previousPubDate, String boardName) {
        AlertList alertList = new AlertList();

        if(rss != null && rss.getEntry() != null && !rss.getEntry().isEmpty()){
            List<Entry> newPosts = getNewPosts(rss.getEntry(), previousPubDate, boardName);

            if(!newPosts.isEmpty()){
                // alert people in channel who have role

				List<String> usersToPing = new ArrayList<>();
				
				Map<String, TopicSubscription> topicSubscriptions = config.getUpdatedTopics();

                for(Entry entry : newPosts){
					String author = entry.getAuthor().getName().trim().toLowerCase();
					String entryId = 
						entry.getId()
							.substring("https://geekhack.org/index.php?topic=".length()).trim()
							.split(".msg")[0];

					// Check if the current entry is in the list of topics we're interested in
					if(topicSubscriptions.containsKey(entryId)){
						TopicSubscription topic = topicSubscriptions.get(entryId);

						// Check if entry is created by the author we're interested in
						if(author.equals(topic.getTopicAuthor())){
							// Alert all of the users subscribed
							usersToPing.addAll(topic.getSubscribers().values());
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