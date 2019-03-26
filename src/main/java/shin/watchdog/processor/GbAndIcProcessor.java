package shin.watchdog.processor;

import java.util.List;

import shin.watchdog.data.GeekhackThread;

/**
 * Class which will process new Geekhack GB and IC threads
 */
public class GbAndIcProcessor extends GeekhackProcessor {

    private final String IC_ROLE = "<@&477264441319096321>";
    private final String GB_ROLE = "<@&477264488983429130>";
    private final String IC_CHANNEL = "https://discordapp.com/api/webhooks/477261547517902848/eq1z6lMMo4-xdz5WAw3xK9DXKFWBUjPwunbeCHwJbRBYNVToqUailAVEB4-08yc8FyHh";
    private final String GB_CHANNEL = "https://discordapp.com/api/webhooks/477261735271858176/atBPCQzWMAj_k6PVrJTMqggwaoEnQ7Hz4HlHjyp6hmfGrdIKgNEbbD9hMrmUms3Y5hVq";

    public GbAndIcProcessor(String boardName, String rssUrl, String boardId, String limit, String subAction) {
        super(boardName, rssUrl, boardId, limit, subAction);
    }

    @Override
    public void processHelper(List<GeekhackThread> newThreads) {
        sendAlert(newThreads);
    }

    /**
     * Sends an alert for the list of new threads to the configured Discord channel and role
     * @param newThreads The list of new threads
     */
    private void sendAlert(List<GeekhackThread> newThreads){
        switch(boardId){
            case "70":
                geekhackMessageService.sendMessage(newThreads, GB_CHANNEL, GB_ROLE);
                break;
            case "132":
                geekhackMessageService.sendMessage(newThreads, IC_CHANNEL, IC_ROLE);
                break;
            default:
                logger.error("what the aass");
        }
    }

}