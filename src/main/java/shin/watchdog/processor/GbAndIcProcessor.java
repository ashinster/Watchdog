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

    private final String IC_ROLE = "<@&477264441319096321>";
    private final String GB_ROLE = "<@&477264488983429130>";
    private final String IC_CHANNEL = "https://discordapp.com/api/webhooks/477261547517902848/eq1z6lMMo4-xdz5WAw3xK9DXKFWBUjPwunbeCHwJbRBYNVToqUailAVEB4-08yc8FyHh";
    private final String GB_CHANNEL = "https://discordapp.com/api/webhooks/477261735271858176/atBPCQzWMAj_k6PVrJTMqggwaoEnQ7Hz4HlHjyp6hmfGrdIKgNEbbD9hMrmUms3Y5hVq";

    public GbAndIcProcessor(String rssUrl, String boards, String limit, String subAction) {
        super(rssUrl, boards, limit, subAction);
    }

    @Override
    public void processHelper(List<Entry> newPosts) {

        List<Alert> icAlerts = new ArrayList<>();
        List<Alert> gbAlerts = new ArrayList<>();

        // Check each entry to see if we need to alert a role
        for (Entry entry : newPosts) {
            Alert alert = new Alert(entry);

            // Check if we need to alert any additional recipients
            if (!newTopicChecker.isAlertListEmpty() && newTopicChecker.check(entry)) {
                // If there's a match, then get the alert recipient
                String author = entry.getAuthor().getName().trim().toLowerCase();
                String roleId = newTopicChecker.getRecipientForTopic(author);

                alert.setRecipient(roleId);
            }

            switch (entry.getCategory().getTerm()) {
                case "70":
                    gbAlerts.add(alert);
                    break;
                case "132":
                    icAlerts.add(alert);
                    break;
                default:
                    logger.warn("Category for \"{}\" has category: {}", entry.getId(), entry.getCategory().getTerm());
            }
        }

        sendAlerts(icAlerts, gbAlerts);
    }

    /**
     * Spins up threads to send the alerts for ic and gb
     */
    private void sendAlerts(List<Alert> icAlerts, List<Alert> gbAlerts) {
        if(!icAlerts.isEmpty()){
            new Thread(() -> {
                geekhackMessageService.sendMessage(icAlerts, IC_CHANNEL, IC_ROLE);
            }).start();
        }

        if(!gbAlerts.isEmpty()){
            new Thread(() -> {
                geekhackMessageService.sendMessage(gbAlerts, GB_CHANNEL, GB_ROLE);
            }).start();
        }
    }

	@Override
	public boolean isAlertListEmpty() {
		return newTopicChecker.isAlertListEmpty();
	}

}