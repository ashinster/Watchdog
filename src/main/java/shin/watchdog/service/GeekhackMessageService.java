package shin.watchdog.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import shin.watchdog.actions.SendPrivateMessage;
import shin.watchdog.data.Entry;

@Service
public class GeekhackMessageService{

    final static Logger logger = LoggerFactory.getLogger(GeekhackMessageService.class);

    @Value("${isDebug}")
    private boolean isDebug;

	private SimpleDateFormat sdfLocal;

	public GeekhackMessageService(){
		this.sdfLocal = new SimpleDateFormat("EEE, dd MMM yyyy h:mm:ss a z");
    }

    public GeekhackMessageService(boolean isDebug){
        this();
        this.isDebug = isDebug;
    }

    public boolean sendMessage(String boardName, List<Entry> potentialPosts, List<String> usersToPing, String webhookUrl, String roleId){
        StringBuilder message = new StringBuilder();

        if(isDebug){
            if(!roleId.isEmpty()){
                roleId = "@FakeRole";
            }
        }

        // If topic has no specific users interested in it, don't add extra "And also" text
        if(!usersToPing.isEmpty()){
            String personalizedMessage = "\n " + String.join(", ", usersToPing);
            message.append(roleId).append(personalizedMessage).append("\n\n");
        } else {
            message.append(roleId).append("\n\n");
        }

        for(Entry siteData : potentialPosts){
            Entry post = (Entry) siteData;
            String localDate = sdfLocal.format(Instant.parse(post.getPublished()).toEpochMilli());

            String prettyTitle = StringEscapeUtils.unescapeHtml4(post.getTitle());

            message.append("\"" + prettyTitle + "\" by " + post.getAuthor().getName()).append("\n\n");
            message.append("Posted on " + localDate).append("\n\n");
            message.append(post.getId()).append("\n\n");
        }

        if(!isDebug){
            return SendPrivateMessage.sendPM("New " + boardName + " Found on Geekhack", message.toString(), webhookUrl);
        } else {
            String debugChannel = "https://discordapp.com/api/webhooks/477287919103639555/7LBWCz1DrYdMN0VHhv1hxpREIYhxniH0VKV0ZQ-abgZlZmxQfWsJ-Ec_KQCOJqB9Wn1L";
            return SendPrivateMessage.sendPM("New " + boardName + " Found on Geekhack", message.toString(), debugChannel);
        }
    }
}