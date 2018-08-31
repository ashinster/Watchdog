package shin.watchdog.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import shin.watchdog.data.Alert;
import shin.watchdog.data.atom.Entry;
import shin.watchdog.interfaces.Checker;

public class ThreadUpdatesProcessor extends GeekhackProcessor {

    @Autowired
    Checker updatedTopicChecker;

    public ThreadUpdatesProcessor(String rssUrl, String boardName, String channelUrl, String roleId) {
        super(rssUrl, boardName, channelUrl, roleId);
    }

    @Override
    public void processHelper(List<Entry> newPosts) {
        List<Alert> threadUpdateAlerts = new ArrayList<>();

        // Check each entry to see if we need to alert a role
        for (Entry entry : newPosts) {
            if (updatedTopicChecker.check(entry)) {
                // If there's a match, then get the alert recipient
                String author = entry.getAuthor().getName().trim().toLowerCase();
                String roleId = updatedTopicChecker.getRecipientForTopic(author);

                // Add alert for the recipient for this entry
                Alert alert = new Alert(entry);
                alert.setRecipient(roleId);

                threadUpdateAlerts.add(alert);
            }
        }

        // Send message in another thread
        if (!threadUpdateAlerts.isEmpty()) {
            new Thread(() -> {
                geekhackMessageService.sendMessage(boardName, threadUpdateAlerts, channelUrl, roleId);
            }).start();
        }
    }

}