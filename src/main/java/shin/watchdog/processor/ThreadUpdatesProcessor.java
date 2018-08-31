package shin.watchdog.processor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import shin.watchdog.data.Entry;
import shin.watchdog.data.Feed;
import shin.watchdog.interfaces.Checker;

public class ThreadUpdatesProcessor extends GeekhackProcessor {

    @Autowired
    Checker updatedTopicChecker;

    public ThreadUpdatesProcessor(String rssUrl, String boardName, String channelUrl, String roleId) {
        super(rssUrl, boardName, channelUrl, roleId);
    }

    @Override
    public void process(){
        // Get the feed via rss/atom
        Feed rss = postsService.makeCall(this.rssUrl, this.boardName);

        if (rss != null && rss.getEntry() != null && !rss.getEntry().isEmpty()) {
            List<Entry> newPosts = getNewPosts(rss.getEntry());

            if (!newPosts.isEmpty()) {
                // Only send alerts for new comments that we're interested in
                List<Entry> postsToPingFor = new ArrayList<>();

                // list of users/roles to alert
                Set<String> usersToPing = new HashSet<>();

                // Check each entry to see if we need to alert a role
                for (Entry entry : newPosts) {
                    if (updatedTopicChecker.check(entry)) {
                        logger.info("New {} found: \"{}\" by {} ({})", boardName, entry.getTitle(), entry.getAuthor().getName(), entry.getId());
                        // If there's a match, then get the role to alert for
                        String author = entry.getAuthor().getName().trim().toLowerCase();
                        String roleId = updatedTopicChecker.roleIdForTopic(author);

                        // Add the role to ping
                        usersToPing.add(roleId);
                        postsToPingFor.add(entry);
                    }
                }

                // remove posts from list for updated threads process
                if(!postsToPingFor.isEmpty() && !usersToPing.isEmpty()){
                    geekhackMessageService.sendMessage(boardName, postsToPingFor, usersToPing, channelUrl, roleId);
                }

                super.previousPubDate = Instant.parse(newPosts.get(0).getPublished()).toEpochMilli();
            }
        }
    }

}