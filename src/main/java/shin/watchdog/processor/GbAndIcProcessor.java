package shin.watchdog.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import shin.watchdog.data.Alert;
import shin.watchdog.data.atom.Entry;
import shin.watchdog.interfaces.Checker;

public class GbAndIcProcessor extends GeekhackProcessor {

    @Autowired
    Checker newTopicChecker;

    public GbAndIcProcessor(String rssUrl, String boardName, String channelUrl, String roleId) {
        super(rssUrl, boardName, channelUrl, roleId);
    }

    @Override
    public void processHelper(List<Entry> newPosts) {

        List<Alert> icAlert = new ArrayList<>();
        List<Alert> gbAlert = new ArrayList<>();

        // Check each entry to see if we need to alert a role
        for (Entry entry : newPosts) {
            Alert alert = new Alert(entry);

            // Check if we need to alert any additional recipients
            if (newTopicChecker.check(entry)) {
                // If there's a match, then get the alert recipient
                String author = entry.getAuthor().getName().trim().toLowerCase();
                String roleId = newTopicChecker.getRecipientForTopic(author);

                alert.setRecipient(roleId);
            }

            switch (entry.getCategory().getTerm()) {
                case "70":
                    gbAlert.add(alert);
                    break;
                case "132":
                    icAlert.add(alert);
                    break;
                default:
                    logger.warn("Category for \"{}\" has category: {}", entry.getId(), entry.getCategory().getTerm());
            }
        }

        sendAlerts(icAlert, gbAlert);
    }

    /**
     * Spins up threads to send the alerts for ic and gb
     */
    private void sendAlerts(List<Alert> icAlert, List<Alert> gbAlert) {
        new Thread(() -> {
            geekhackMessageService.sendMessage(boardName, icAlert, channelUrl, roleId);
        }).start();

        new Thread(() -> {
            geekhackMessageService.sendMessage(boardName, gbAlert, channelUrl, roleId);
        }).start();
    }

}