package shin.watchdog.service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import shin.watchdog.actions.SendPrivateMessage;
import shin.watchdog.config.GeekhackConfig;
import shin.watchdog.data.GeekhackThread;

@Service
public class GeekhackMessageService{

    final static Logger logger = LoggerFactory.getLogger(GeekhackMessageService.class);

    @Value("${isDebug:false}")
    private boolean isDebug;

    @Autowired
    private GeekhackConfig geekhackConfig;

	private SimpleDateFormat sdfLocal;

	public GeekhackMessageService(){
		this.sdfLocal = new SimpleDateFormat("EEE, dd MMM yyyy h:mm:ss a z");
    }

    public boolean sendMessage(List<GeekhackThread> alerts, String webhookUrl){
        return sendMessage(alerts, webhookUrl, "");
    }

    /**
     * Takes in a list of new Geekhack threads, constructs and sends a Discord message to the specified channel and role ID.
     * @param newThreads The list of new threads to alert for.
     * @param webhookUrl The channel to post the message to
     * @param roleRecipient The Discord role ID to ping
     * @return True only if the message was successfully sent.
     */
    public boolean sendMessage(List<GeekhackThread> newThreads, String webhookUrl, String roleRecipient){
        StringBuilder finalMessage = new StringBuilder();

        if(isDebug){
            roleRecipient = "@FakeRole";
        }

        // Ping the role
        finalMessage.append(roleRecipient).append("\n\n");

        // Iterate through all the new posts and construct a message
        for(GeekhackThread newThread : newThreads){

            // Unescape the title to avoid weird characters
            String prettyTitle = StringEscapeUtils.unescapeHtml4(newThread.getTitle());

            // Get the post date
            String localDate = sdfLocal.format(Instant.parse(newThread.getPublished()).toEpochMilli());

            // Construct the message
            String message = String.format(geekhackConfig.getMessageFormat(),
                prettyTitle.trim(),
                newThread.getAuthor(),
                localDate,
                newThread.getId()
            ); 

            // Append the new thread alert to the final message
            finalMessage.append(message);
        }

        if(!isDebug){
            return SendPrivateMessage.sendPM(finalMessage.toString(), webhookUrl);
        } else {
            // Send to debug channel instead
            webhookUrl = "https://discordapp.com/api/webhooks/477287919103639555/7LBWCz1DrYdMN0VHhv1hxpREIYhxniH0VKV0ZQ-abgZlZmxQfWsJ-Ec_KQCOJqB9Wn1L";
            return SendPrivateMessage.sendPM(finalMessage.toString(), webhookUrl);
        }
    }
}