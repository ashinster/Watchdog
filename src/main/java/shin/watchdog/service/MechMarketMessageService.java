package shin.watchdog.service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;

import com.google.gson.JsonObject;

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
public class MechMarketMessageService {
    final static Logger logger = LoggerFactory.getLogger(MechMarketMessageService.class);

    @Value("${isDebug:false}")
    private boolean isDebug;

    @Autowired
    private GeekhackConfig geekhackConfig;

	private SimpleDateFormat sdfLocal;

	public MechMarketMessageService(){
		this.sdfLocal = new SimpleDateFormat("EEE, dd MMM yyyy h:mm:ss a z");
    }

    public boolean sendMessage(List<JsonObject> newPosts, String webhookUrl, String roleRecipient){
        StringBuilder finalMessage = new StringBuilder();

        if(isDebug){
            roleRecipient = "@FakeRole";
        }

        // Ping the role
        finalMessage.append(roleRecipient).append("\n\n");

        // Iterate through all the new posts and construct a message
        for(JsonObject newThread : newPosts){

            // Unescape the title to avoid weird characters
            String prettyTitle = newThread.get("title").toString().replace("\"", "");

            // Get the post date
            String localDate = sdfLocal.format(newThread.get("created").getAsLong()*1000);

            // Construct the message
            String message = String.format(geekhackConfig.getMessageFormat(),
                prettyTitle.trim(),
                newThread.get("author").toString().replace("\"", ""),
                localDate,
                newThread.get("url").toString().replace("\"", "")
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