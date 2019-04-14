package shin.watchdog.processor;

import java.time.Instant;
import java.util.List;

import shin.watchdog.data.GeekhackThread;
import shin.watchdog.data.atom.Entry;

/**
 * InterestCheckProcessor
 */
public class InterestCheckProcessor extends GeekhackProcessor {

    private long lastPubDate;

    public InterestCheckProcessor(String boardName, String rssUrl, String boardId, String limit, String subAction, String alertRoleId, String alertRoleChannelUrl) {
        super(boardName, rssUrl, boardId, limit, subAction, alertRoleId, alertRoleChannelUrl);
        this.lastPubDate = Instant.now().toEpochMilli();
    }

    @Override
    public void processHelper(List<GeekhackThread> newThreads) {

        // Get publish date of newest post from list
        this.lastPubDate = Instant.parse(newThreads.get(0).getPublished()).toEpochMilli();

        sendAlert(newThreads);
    }

    @Override
    public boolean filter(Entry entry){
        boolean isNew = false;
        if(isDebug) {
            logger.info("New IC thread found: \"{}\" by {} ({})", entry.getTitle(), entry.getAuthor(), entry.getId());
            isNew = true;
        } else {
            // Check publish date
            if(Instant.parse(entry.getPublished()).toEpochMilli() > this.lastPubDate) {
                if(!entry.getTitle().startsWith("Re:")) {
                    logger.info("New IC thread found: \"{}\" by {} ({})", entry.getTitle(), entry.getAuthor(), entry.getId());
                    isNew = true;
                } else {
                    logger.info("IC entry starting with 'Re:' found: {} ({})", entry.getTitle(), entry.getId());
                }
            }
        }      
        return isNew;
    }

    @Override
    boolean sendAlert(List<GeekhackThread> newThreads) {
        return geekhackMessageService.sendMessage(newThreads, alertRoleChannelUrl, alertRoleId);
    }    

}