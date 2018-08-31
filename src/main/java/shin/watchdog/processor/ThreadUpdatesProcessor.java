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

    private final String UPDATES_CHANNEL = "https://discordapp.com/api/webhooks/483098053381849093/VkwqJi4zNO65ydNGmtn42Ac4eNOZR3DLcglRDclNqiJW4A0G7hQtym1Bv5jkJgM8GWJq";

    public ThreadUpdatesProcessor(String rssUrl, String boardName) {
        super(rssUrl, boardName);
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
                geekhackMessageService.sendMessage(threadUpdateAlerts, UPDATES_CHANNEL);
            }).start();
        }
    }

    @Override
    public boolean isAlertListEmpty() {
        return updatedTopicChecker.isAlertListEmpty();
    }

}