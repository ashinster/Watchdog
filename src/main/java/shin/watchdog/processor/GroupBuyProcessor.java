package shin.watchdog.processor;

import java.util.List;

import shin.watchdog.data.GeekhackThread;
import shin.watchdog.data.atom.Entry;

/**
 * GroupBuyProcessor
 */
public class GroupBuyProcessor extends GeekhackProcessor {

    private float lastTopicId; 

    public GroupBuyProcessor(String boardName, String rssUrl, String boardId, String limit, String subAction, String alertRoleId, String alertRoleChannelUrl) {
        super(boardName, rssUrl, boardId, limit, subAction, alertRoleId, alertRoleChannelUrl);
    }

    @Override
    public void processHelper(List<GeekhackThread> newThreads) {

        if(lastTopicId != 0.0f) {
            sendAlert(newThreads);
        }

        this.lastTopicId = Float.parseFloat(newThreads.get(0).getId().substring("https://geekhack.org/index.php?topic=".length()));
    }

    @Override
    public boolean filter(Entry entry){
        boolean isNew = false;
        if(isDebug){
            isNew = true;
        } else {
            float postId = Float.parseFloat(entry.getId().substring("https://geekhack.org/index.php?topic=".length()));
            if(!entry.getTitle().startsWith("Re:")) {
                if(postId > this.lastTopicId){
                    logger.info("New GB thread found: \"{}\" by {} ({})", entry.getTitle(), entry.getAuthor(), entry.getId());
                    isNew = true;
                }
            } else {
                logger.info("GB entry starting with 'Re:' found: {} ({})", entry.getTitle(), entry.getId());
            }
        }
        return isNew;
    }

    @Override
    boolean sendAlert(List<GeekhackThread> newThreads) {
        return geekhackMessageService.sendMessage(newThreads, alertRoleChannelUrl, alertRoleId);
    }    
    
}